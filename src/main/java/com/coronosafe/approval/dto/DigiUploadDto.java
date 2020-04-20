package com.coronosafe.approval.dto;

public class DigiUploadDto {
    private long uploadId;
    private String fileName;
    private boolean sanctionStatus;

    public DigiUploadDto(long uploadId,String fileName,boolean sanctionStatus){
        this.uploadId=uploadId;
        this.fileName=fileName;
        this.sanctionStatus=sanctionStatus;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isSanctionStatus() {
        return sanctionStatus;
    }

    public void setSanctionStatus(boolean sanctionStatus) {
        this.sanctionStatus = sanctionStatus;
    }
}
