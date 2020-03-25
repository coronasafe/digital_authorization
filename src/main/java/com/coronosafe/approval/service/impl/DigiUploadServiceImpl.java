package com.coronosafe.approval.service.impl;

import com.coronosafe.approval.jdbc.DigiUploadsRepository;
import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.service.DigiUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class DigiUploadServiceImpl implements DigiUploadService {
    @Autowired
    DigiUploadsRepository digiUploadsRepository;
    @Autowired
    DigiUserRepository digiUserRepository;

    @Override
    public void deleteFile(int id) {
        digiUploadsRepository.deleteById(id);
    }

    @Override
    public void saveFile(byte[] file, LocalDateTime uploadDate,String userName) {
        DigiUser digiUser = digiUserRepository.findByUserName(userName);
        DigiUploads digiUploads = new DigiUploads(file,uploadDate);
        digiUploads.setDigiUser(digiUser);
        digiUploadsRepository.save(digiUploads);
    }
}
