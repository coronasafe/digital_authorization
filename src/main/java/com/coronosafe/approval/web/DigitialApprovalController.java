package com.coronosafe.approval.web;

import com.coronosafe.approval.auth.UserDetailsServiceImpl;
import com.coronosafe.approval.constants.DigiConstants;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.service.DigiUploadService;
import com.coronosafe.approval.service.DigiUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class DigitialApprovalController {

    private static final Logger log = LoggerFactory.getLogger(DigitialApprovalController.class);

    @Autowired
    DigiUserService digiUserService;

    @Autowired
    DigiUploadService digiUploadService;

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
        model.addAttribute("currentUser", loggedInUser.getUserName());

        if(loggedInUser!=null && DigiConstants.MISSION_DIRECTOR_ROLE.equals(loggedInUser.getRole().getRoleName())){
            model.addAttribute("missionDirector", Boolean.TRUE);
            model.addAttribute("uploads",digiUploadService.retrieveDigiUploads());
        }
        return "upload";
    }

    @PostMapping("/success")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,@RequestParam(required = false,name="id") Integer uploadId,
                                   RedirectAttributes redirectAttributes, @RequestParam(required = false,name = "currentUser") String userName) {
        if(uploadId!=null){
            digiUploadService.updateSanctions(uploadId);
        }else {
            try {
                log.info("The user name "+userName);
                digiUploadService.saveFile(file, LocalDateTime.now(), userName);
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded " + file.getOriginalFilename() + "!");
            } catch (IOException ioEx) {
                log.error("IOException while uploading file " + file.getOriginalFilename());
                redirectAttributes.addFlashAttribute("message",
                        "Please upload the file again!");
                return "upload";
            }
        }
        return "success";
    }


    @GetMapping("/approve")
    public void approve(){

    }
}
