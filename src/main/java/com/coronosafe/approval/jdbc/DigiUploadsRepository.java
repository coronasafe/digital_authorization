package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiUploads;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DigiUploadsRepository extends CrudRepository<DigiUploads,Integer> {
    DigiUploads findById(int id);

    @Query("select digiUploads from DigiUploads digiUploads join DigiUser digiUser ON digiUploads.digiUser=digiUser.id WHERE digiUser.id=?1")
    Optional<List<DigiUploads>> findByDigiUser(int userId);
}

