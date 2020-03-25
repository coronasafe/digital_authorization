package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiUploads;
import org.springframework.data.repository.CrudRepository;

public interface DigiUploadsRepository extends CrudRepository<DigiUploads,Integer> {
    DigiUploads findById(int id);
}

