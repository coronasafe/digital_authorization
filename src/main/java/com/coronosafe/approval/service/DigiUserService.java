package com.coronosafe.approval.service;

import com.coronosafe.approval.jdbc.data.DigiUser;


public interface DigiUserService {
    DigiUser findUser(String userName);
}
