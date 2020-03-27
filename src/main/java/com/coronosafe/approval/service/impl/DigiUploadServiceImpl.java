package com.coronosafe.approval.service.impl;

import com.coronosafe.approval.constants.DigiConstants;
import com.coronosafe.approval.dto.DigiUploadDto;
import com.coronosafe.approval.jdbc.DigiSanctionsRepository;
import com.coronosafe.approval.jdbc.DigiUploadsRepository;
import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiSanctions;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.service.DigiUploadService;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class DigiUploadServiceImpl implements DigiUploadService {
    private static final Logger log = LoggerFactory.getLogger(DigiUploadServiceImpl.class);
    @Autowired
    DigiUploadsRepository digiUploadsRepository;
    @Autowired
    DigiUserRepository digiUserRepository;
    @Autowired
    DigiSanctionsRepository digiSanctionsRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void saveFile(MultipartFile file, LocalDateTime uploadDate, String userName) throws IOException {
        log.info("Save the uploaded file");
        DigiUser digiUser = digiUserRepository.findByUserName(userName);
        DigiUploads digiUploads = new DigiUploads(file.getBytes(),uploadDate);
        digiUploads.setDigiUser(digiUser);
        digiUploadsRepository.save(digiUploads);

        DigiSanctions digiSanctions = new DigiSanctions(Boolean.FALSE,file.getOriginalFilename());
        digiSanctions.setDigiUploads(digiUploads);
        digiSanctionsRepository.save(digiSanctions);
        sendUploadEmail(file);
    }

    public void sendUploadEmail(MultipartFile file) {

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            DigiUser user = digiUserRepository.findDigiUserFromRole(DigiConstants.MISSION_DIRECTOR_ROLE);
            helper.setTo(user.getEmail());
            helper.setText("A document has been sent for your review");
            ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes());
            helper.addAttachment(file.getOriginalFilename(),byteArrayResource);

            javaMailSender.send(message);
        }catch(MessagingException mse){
            log.error("MessagingException while sending email to the State Mission Director: "+mse);
        }catch(IOException ioe){
            log.error("IOException while sending email to the State Mission Director: "+ioe);
        }
    }

    @Override
    public List<DigiUploadDto> retrieveDigiUploads() {
        log.info("Retrieve the uploads for the Mission Director");

        DigiUser digiUser = digiUserRepository.findDigiUserFromRole(DigiConstants.INSTITUTION_ROLE);
        Optional<List<DigiUploads>> optionalUploadList = digiUploadsRepository.findByDigiUser(digiUser.getId());
        if (optionalUploadList.isPresent()) {
            log.info("Got the uploads for the Mission Director");
            List<DigiUploadDto> uploadDtoList = new ArrayList<>();
            List<DigiUploads> uploadsList = optionalUploadList.get();
            uploadsList.forEach(uploads -> {
                try {
                DigiSanctions digiSanctions = digiSanctionsRepository.findByDigiUploads(uploads.getUploadId());
                File uploadedFile = new File("./uploads/" + digiSanctions.getFileName());
                FileUtils.writeByteArrayToFile(uploadedFile, uploads.getUploadedFile());
                DigiUploadDto uploadDto = new DigiUploadDto(uploads.getUploadId(), digiSanctions.getFileName(), digiSanctions.getSanctionStatus().booleanValue(),uploadedFile.getAbsolutePath());
                uploadDtoList.add(uploadDto);
            }catch(IOException ioe){
                log.error("IOException while writing to the file "+ioe);
            }
            });
            return uploadDtoList;
        }

        return Collections.emptyList();
    }

    @Override
    public void updateSanctions(int uploadId) {
        DigiSanctions digiSanctions = digiSanctionsRepository.findByDigiUploads(uploadId);
        digiSanctions.setSanctionStatus(Boolean.TRUE);
        digiSanctionsRepository.save(digiSanctions);
        sendSanctionedEmail();
    }

    public void sendSanctionedEmail() {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            DigiUser user = digiUserRepository.findDigiUserFromRole(DigiConstants.OFFICIAL_ROLE);
            helper.setTo(user.getEmail());
            helper.setText("State Mission Director has reviewed a request to set up at CCC at so and so location");

            javaMailSender.send(message);
        }catch(MessagingException mse){
            log.error("MessagingException while sending email to the Government Official: "+mse);
        }
    }
}
