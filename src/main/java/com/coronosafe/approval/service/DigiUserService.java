package com.coronosafe.approval.service;

import com.coronosafe.approval.form.RegisterUser;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.jdbc.data.DigiUserRole;

import java.util.List;


public interface DigiUserService {
    DigiUser findUser(String userName);

    boolean saveUser(RegisterUser registerUser);

    List<DigiUserRole> getUserRoles();
}
