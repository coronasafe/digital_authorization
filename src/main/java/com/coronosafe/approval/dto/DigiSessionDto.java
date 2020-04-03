package com.coronosafe.approval.dto;

public class DigiSessionDto {
    private String userName;
    private String secureString;
    private long uploadId;

    public DigiSessionDto(String userName,String secureString,long uploadId){
        this.userName = userName;
        this.secureString=secureString;
        this.uploadId=uploadId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSecureString() {
        return secureString;
    }

    public void setSecureString(String secureString) {
        this.secureString = secureString;
    }

    public long getUploadId() {
        return uploadId;
    }

    public void setUploadId(long uploadId) {
        this.uploadId = uploadId;
    }
}
