package com.coronosafe.approval.service;

import com.coronosafe.approval.dto.DigiUploadDto;
import com.coronosafe.approval.jdbc.data.DigiSanctions;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import com.coronosafe.approval.jdbc.data.DigiUser;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DigiUploadService {

    long saveFile(MultipartFile file, LocalDateTime uploadDate, String userName) throws IOException;

    List<DigiUploadDto> retrieveDigiUploads();

    void updateSanctions(long uploadId);

    Object prepareEmailWithSignature(String authCode,long uploadId,String userName, String redirect);

    DigiSanctions getDigiSanctionsFromUploadId(long uploadId);

}
