package com.coronosafe.approval.jdbc.data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class DigiUploads {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long uploadId;
    @Column(columnDefinition = "BLOB")
    @Lob
    private byte[] uploadedFile;
    @Column
    private LocalDateTime uploadedDate;
    @OneToOne
    private DigiUser digiUser;
    @Column
    private String fileName;

    protected DigiUploads(){}

    public DigiUploads(byte[] uploadedFile,LocalDateTime uploadedDate,String fileName){
        this.uploadedFile=uploadedFile;
        this.uploadedDate=uploadedDate;
        this.fileName=fileName;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public byte[] getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(byte[] uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public LocalDateTime getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(LocalDateTime uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public DigiUser getDigiUser() {
        return digiUser;
    }

    public void setDigiUser(DigiUser digiUser) {
        this.digiUser = digiUser;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
