package com.coronosafe.approval.web;

import com.coronosafe.approval.auth.UserDetailsServiceImpl;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.service.DigiUploadService;
import com.coronosafe.approval.service.DigiUserService;
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
        return "upload";
    }

    @PostMapping("/uploadSuccess")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes, @ModelAttribute String userName) {
        try {
            digiUploadService.saveFile(file.getBytes(), LocalDateTime.now(),userName);
            redirectAttributes.addFlashAttribute("message",
                    "You successfully uploaded " + file.getOriginalFilename() + "!");
        }catch (IOException ioEx){
            log.error("IOException while uploading file "+file.getOriginalFilename());
            redirectAttributes.addFlashAttribute("message",
                    "Please upload the file again!");
            return "upload";
        }
        return "uploadSuccess";
    }


    @GetMapping("/approve")
    public void approve(){

    }
}
