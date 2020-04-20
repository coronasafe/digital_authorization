package com.coronosafe.approval.web;

import com.coronosafe.approval.constants.DigiConstants;
import com.coronosafe.approval.docusign.DigiSignService;
import com.coronosafe.approval.dto.DigiSessionDto;
import com.coronosafe.approval.form.RegisterUser;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.jdbc.data.DigiUserRole;
import com.coronosafe.approval.service.DigiUploadService;
import com.coronosafe.approval.service.DigiUserService;
import com.coronosafe.approval.util.DigiUtils;
import com.coronosafe.approval.validator.DigiUserValidator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class DigitialApprovalController {

    private static final Logger log = LoggerFactory.getLogger(DigitialApprovalController.class);

    @Autowired
    DigiUserService digiUserService;

    @Autowired
    DigiUploadService digiUploadService;

    @Autowired
    DigiSignService digiSignService;

    @Autowired
    DigiUserValidator digiUserValidator;

    @GetMapping("/addUser")
    public String addUser(Model model){

        model.addAttribute(DigiConstants.REGISTRATION.REGISTER_USER,new RegisterUser());
        return "register";
    }

    @PostMapping("/addUser")
    public String registerUser(@Valid RegisterUser registerUser, BindingResult bindingResult,
                               Model model){

        log.debug("Register user validation "+registerUser.toString());
        digiUserValidator.validate(registerUser,bindingResult);
        if(bindingResult.hasErrors()){
            model.addAttribute(DigiConstants.REGISTRATION.REGISTERED_USER,registerUser);
            return "register";
        }
        digiUserService.saveUser(registerUser);
        model.addAttribute(DigiConstants.REGISTRATION.REGISTERED_USER,registerUser.getUserName());
        return "registerSuccess";
    }

    @GetMapping("/error")
    public String errorPage(){
        log.info("An unexpected error has occured");
        return "error";
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";

    }

    @GetMapping("/upload")
    public String home(Model model, HttpSession session){
        log.info("Upload Page");
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken)
                SecurityContextHolder.getContext().getAuthentication();

        DigiUser loggedInUser = digiUserService.findUser(((UserDetails) authentication.getPrincipal()).getUsername());
        model.addAttribute(DigiConstants.CURRENT_USER, loggedInUser.getUserName());

        if(loggedInUser!=null && DigiConstants.MISSION_DIRECTOR_ROLE.equals(loggedInUser.getRole().getRoleName())){
            model.addAttribute("missionDirector", Boolean.TRUE);
            model.addAttribute("uploads",digiUploadService.retrieveDigiUploads());
        }
        return "upload";
    }


    @PostMapping(value = "/success")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, Model model,
                                   @RequestParam(name = "currentUser") String userName,
                                   HttpSession session) {
        long uploadedId = 0;
        String authUrl = null;
        try {
            log.info("The user name "+userName);
            //Upload the file
            uploadedId = digiUploadService.saveFile(file, LocalDateTime.now(), userName);

            DigiSessionDto digiSessionDto = new DigiSessionDto(userName,DigiUtils.getSecureString(),uploadedId);
            session.setAttribute(DigiConstants.DIGI_SESSION_ATTR,digiSessionDto);

            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");
            // Call the docusign auth service to start the signing process
            authUrl = digiSignService.getAuthCode(digiSessionDto.getSecureString());

        } catch (IOException ioEx) {
            log.error("IOException while uploading file " + file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("message",
                    "Please upload the file again!");
            return "upload";
        }
        if(StringUtils.isNotBlank(authUrl)) {
            return "redirect:" + authUrl;
        }
        return "success";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity downloadFile(@PathVariable String id) {
        log.debug("Download request for the file with id: "+id);
        DigiUploads uploads = digiUploadService.getUploadedFile(Long.parseLong(id));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(DigiConstants.CONTENT_TYPE))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + uploads.getFileName() + "\"")
                .body(uploads.getUploadedFile());
    }

    /**
     * Method that initiates the docusign signing ceremony and serves the callback from the docusign signing ceremony
     * @param code
     * @return
     */
    @GetMapping("/success")
    public Object handleSuccessForSigning(@RequestParam(required = false,name="code") String code,
                                      @SessionAttribute(DigiConstants.DIGI_SESSION_ATTR) DigiSessionDto digiSessionDto,
                                      HttpSession session){

        if(StringUtils.isNotBlank(code)) {
            RedirectView redirectView = (RedirectView)digiUploadService.
                    prepareEmailWithSignature(code, digiSessionDto.getUploadId(),digiSessionDto.getUserName(),
                            "success");
            return redirectView;
        }
        session.removeAttribute(DigiConstants.DIGI_SESSION_ATTR);
        return "success";
    }


    @PostMapping(value = "/updateUpload")
    public String updateUploads(@RequestParam("id") Integer uploadId,
                                @RequestParam(name = "currentUser") String userName,
                                HttpSession session){
        long sanctionId = digiUploadService.getDigiSanctionsFromUploadId(uploadId).getId();
        DigiSessionDto digiSessionDto = new DigiSessionDto(userName,DigiUtils.getSecureString(),sanctionId);
        session.setAttribute(DigiConstants.DIGI_SESSION_ATTR,digiSessionDto);
        digiUploadService.updateSanctions(uploadId);
        // Call the docusign auth service to start the signing process
        String authUrl = digiSignService.getAuthCode(digiSessionDto.getSecureString());
        return "redirect:"+authUrl;
    }

    @GetMapping(value = "/updateUpload")
    public Object updateUploadsForSigning(@RequestParam(required = false,name="code") String code,
                                @SessionAttribute(DigiConstants.DIGI_SESSION_ATTR) DigiSessionDto digiSessionDto,
                                            HttpSession session){
        long sanctionId = digiUploadService.getDigiSanctionsFromUploadId(digiSessionDto.getUploadId()).getId();
        if(StringUtils.isNotBlank(code)) {
            RedirectView redirectView = (RedirectView) digiUploadService.prepareEmailWithSignature(code, sanctionId,digiSessionDto.getUserName(),"updateUpload");
            return redirectView;
        }
        session.removeAttribute(DigiConstants.DIGI_SESSION_ATTR);
        return "success";
    }

    @ModelAttribute(DigiConstants.REGISTRATION.ROLES_LIST)
    private List<DigiUserRole> getUserRoles(){
        return digiUserService.getUserRoles();
    }


}
