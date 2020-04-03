package com.coronosafe.approval.web;

import com.coronosafe.approval.constants.DigiConstants;
import com.coronosafe.approval.docusign.DigiSignService;
import com.coronosafe.approval.dto.DigiSessionDto;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.service.DigiUploadService;
import com.coronosafe.approval.service.DigiUserService;
import com.coronosafe.approval.util.DigiUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class DigitialApprovalController {

    private static final Logger log = LoggerFactory.getLogger(DigitialApprovalController.class);

    @Autowired
    DigiUserService digiUserService;

    @Autowired
    DigiUploadService digiUploadService;

    @Autowired
    DigiSignService digiSignService;

    @GetMapping("/addUser")
    public String addUser(Model model){
        return "addUser";
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

}
