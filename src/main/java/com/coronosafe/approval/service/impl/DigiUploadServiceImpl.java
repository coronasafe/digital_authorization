package com.coronosafe.approval.service.impl;

import com.coronosafe.approval.constants.DigiConstants;
import com.coronosafe.approval.docusign.DigiSignService;
import com.coronosafe.approval.dto.DigiUploadDto;
import com.coronosafe.approval.dto.DocuSignUserInfoDto;
import com.coronosafe.approval.jdbc.DigiSanctionsRepository;
import com.coronosafe.approval.jdbc.DigiUploadsRepository;
import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiSanctions;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.service.DigiUploadService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    private DigiSignService digiSignService;

    @Override
    public long saveFile(MultipartFile file, LocalDateTime uploadDate, String userName) throws IOException {
        log.info("Save the uploaded file");
        long uploadId = 0;
        Optional<DigiUser> digiUser = digiUserRepository.findByUserName(userName);
        if(digiUser.isPresent()) {
            DigiUploads digiUploads = new DigiUploads(file.getBytes(), uploadDate,file.getOriginalFilename());
            digiUploads.setDigiUser(digiUser.get());
            DigiUploads uploadedFile = digiUploadsRepository.save(digiUploads);

            DigiSanctions digiSanctions = new DigiSanctions(Boolean.FALSE);
            digiSanctions.setDigiUploads(digiUploads);
            digiSanctionsRepository.save(digiSanctions);
            uploadId = uploadedFile.getUploadId();

        }
        return uploadId;
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
                Optional<DigiSanctions> digiSanctions = digiSanctionsRepository.findByDigiUploads(uploads.getUploadId());
                if(digiSanctions.isPresent()) {
                    DigiSanctions digiSanctionsObj = digiSanctions.get();
                    DigiUploadDto uploadDto = new DigiUploadDto(uploads.getUploadId(), uploads.getFileName(),
                            digiSanctionsObj.getSanctionStatus().booleanValue());
                    uploadDtoList.add(uploadDto);
                 }
            });
            return uploadDtoList;
        }

        return Collections.emptyList();
    }

    @Override
    public void updateSanctions(long uploadId) {
        DigiSanctions digiSanctions = getDigiSanctionsFromUploadId(uploadId);
        digiSanctions.setSanctionStatus(Boolean.TRUE);
        digiSanctionsRepository.save(digiSanctions);
    }


    /**
     * Method to request for the digital signature
     * @param authCode
     * @param emailUniqueId - can be upload id or sanction id
     * @param userName
     * @param redirect - url to which redirect has to be happen after signing
     * @return
     */
    @Override
    public Object prepareEmailWithSignature(String authCode,long emailUniqueId,String userName, String redirect){
        Optional<DigiUser> digiUser = digiUserRepository.findByUserName(userName);
        if(digiUser.isPresent()) {
            String accessToken = digiSignService.getAccessToken(authCode).getAccess_token();
            DocuSignUserInfoDto docuSignUserInfoDto = digiSignService.getUserInfo(accessToken);
            return digiSignService.prepareDocumentSignature(DigiConstants.EMAIL_MISSION_DIRECTOR,
                    ((Long) emailUniqueId).toString(),
                    digiUser.get(), accessToken, docuSignUserInfoDto, redirect);
        }
        return null;
    }

    /**
     * Method to get the sanction details from uploadId
     * @param uploadId
     * @return
     */
    @Override
    public DigiSanctions getDigiSanctionsFromUploadId(long uploadId) {
        Optional<DigiSanctions> digiSanctions = digiSanctionsRepository.findByDigiUploads(uploadId);
        if(digiSanctions.isPresent()){
            return digiSanctions.get();
        }
        return null;
    }

    @Override
    public DigiUploads getUploadedFile(long uploadedId) {
        Optional<DigiUploads> uploadedFile = digiUploadsRepository.findById(uploadedId);
        if(uploadedFile.isPresent()){
            log.debug("Got the requested file to be downloaded");
            return uploadedFile.get();
        }
        return null;
    }
}
