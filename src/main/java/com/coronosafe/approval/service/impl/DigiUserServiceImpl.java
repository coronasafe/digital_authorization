package com.coronosafe.approval.service.impl;

import com.coronosafe.approval.service.DigiUserService;
import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DigiUserServiceImpl implements DigiUserService {
    @Autowired
    DigiUserRepository digiUserRepository;

    @Override
    public DigiUser findUser(String userName) {
        Optional<DigiUser> digiUser =digiUserRepository.findByUserName(userName);
        if(digiUser.isPresent()){
            return digiUser.get();
        }
        return null;
    }
}
