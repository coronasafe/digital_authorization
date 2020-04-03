package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface DigiUserRepository extends CrudRepository<DigiUser,Long> {
    Optional<DigiUser> findByUserName(String userName);

    @Query("SELECT digi from DigiUser digi join DigiUserRole digiRole ON digi.role=digiRole.role_id where digiRole.roleName=?1")
    DigiUser findDigiUserFromRole(String roleName);
}
