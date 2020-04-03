package com.coronosafe.approval.jdbc.data;

import javax.persistence.*;

@Entity
public class DigiSanctions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(columnDefinition = "boolean default false")
    private Boolean sanctionStatus;
    @Column
    private String fileName;
    @OneToOne
    private DigiUploads digiUploads;

   protected DigiSanctions(){}

   public DigiSanctions(Boolean sanctionStatus,String fileName){
       this.sanctionStatus=sanctionStatus;
       this.fileName=fileName;
   }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getSanctionStatus() {
        return sanctionStatus;
    }

    public void setSanctionStatus(Boolean sanctionStatus) {
        this.sanctionStatus = sanctionStatus;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public DigiUploads getDigiUploads() {
        return digiUploads;
    }

    public void setDigiUploads(DigiUploads digiUploads) {
        this.digiUploads = digiUploads;
    }
}
