package com.coronosafe.approval.jdbc;

import com.coronosafe.approval.jdbc.data.DigiUserRole;
import org.springframework.data.repository.CrudRepository;

public interface DigiUserRoleRepository extends CrudRepository<DigiUserRole,Long> {
    public DigiUserRole findByRoleName(String roleName);
}
