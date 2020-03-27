package com.coronosafe.approval.service;

import com.coronosafe.approval.dto.DigiUploadDto;
import com.coronosafe.approval.jdbc.data.DigiUploads;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DigiUploadService {

    void saveFile(MultipartFile file, LocalDateTime uploadDate, String userName) throws IOException;

    List<DigiUploadDto> retrieveDigiUploads();

    void updateSanctions(int uploadId);

}
