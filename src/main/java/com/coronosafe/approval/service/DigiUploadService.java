package com.coronosafe.approval.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DigiUploadService {
    void deleteFile(int id);

    void saveFile(byte[] file, LocalDateTime uploadDate,String userName);
}
