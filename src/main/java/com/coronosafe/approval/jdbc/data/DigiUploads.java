package com.coronosafe.approval.jdbc.data;

import org.hibernate.annotations.Generated;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class DigiUploads {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int uploadId;
    @Column
    private byte[] uploadedFile;
    @Column
    private LocalDateTime uploadedDate;
    @OneToOne
    private DigiUser digiUser;

    protected DigiUploads(){}

    public DigiUploads(byte[] uploadedFile,LocalDateTime uploadedDate){
        this.uploadedFile=uploadedFile;
        this.uploadedDate=uploadedDate;
    }

    public int getUploadId() {
        return uploadId;
    }

    public void setUploadId(int uploadId) {
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
}
