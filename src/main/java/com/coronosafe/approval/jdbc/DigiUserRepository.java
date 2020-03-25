package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiUser;
import org.springframework.data.repository.CrudRepository;

public interface DigiUserRepository extends CrudRepository<DigiUser,Long> {
    DigiUser findByUserName(String userName);
}
