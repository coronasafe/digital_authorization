package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiSanctions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DigiSanctionsRepository extends CrudRepository<DigiSanctions,Integer> {
    DigiSanctions findById(int id);

    @Query("select digiSanction from DigiSanctions digiSanction join DigiUploads digiUpload ON digiSanction.digiUploads=digiUpload.uploadId WHERE digiUpload.uploadId=?1")
    DigiSanctions findByDigiUploads(int uploadId);
}

