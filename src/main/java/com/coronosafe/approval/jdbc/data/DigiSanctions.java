package com.coronosafe.approval.jdbc.data;

import javax.persistence.*;

@Entity
public class DigiSanctions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(columnDefinition = "boolean default false")
    private Boolean sanctionStatus;
    @OneToOne
    private DigiUploads digiUploads;

   protected DigiSanctions(){}

   public DigiSanctions(Boolean sanctionStatus){
       this.sanctionStatus=sanctionStatus;
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

    public DigiUploads getDigiUploads() {
        return digiUploads;
    }

    public void setDigiUploads(DigiUploads digiUploads) {
        this.digiUploads = digiUploads;
    }
}
