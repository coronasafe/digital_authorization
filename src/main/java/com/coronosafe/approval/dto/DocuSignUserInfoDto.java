package com.coronosafe.approval.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties({"sub","name","given_name","family_name","created","email","is_default","account_name","organization","links"})
public class DocuSignUserInfoDto {
    private String account_id;
    private String base_uri;

    public DocuSignUserInfoDto(String account_id, String base_uri) {
        this.account_id = account_id;
        this.base_uri = base_uri;
    }

    public DocuSignUserInfoDto() {
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getBase_uri() {
        return base_uri;
    }

    public void setBase_uri(String base_uri) {
        this.base_uri = base_uri;
    }
}
