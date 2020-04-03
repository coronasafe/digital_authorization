package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiSanctions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DigiSanctionsRepository extends CrudRepository<DigiSanctions,Long> {
    @Query("select digiSanction from DigiSanctions digiSanction join DigiUploads digiUpload ON digiSanction.digiUploads=digiUpload.uploadId WHERE digiUpload.uploadId=?1")
    Optional<DigiSanctions> findByDigiUploads(long uploadId);
}

