package com.coronosafe.approval.service.impl;

import com.coronosafe.approval.form.RegisterUser;
import com.coronosafe.approval.jdbc.DigiUserRoleRepository;
import com.coronosafe.approval.jdbc.data.DigiUserRole;
import com.coronosafe.approval.service.DigiUserService;
import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DigiUserServiceImpl implements DigiUserService {
    @Autowired
    DigiUserRepository digiUserRepository;

    @Autowired
    DigiUserRoleRepository digiUserRoleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public DigiUser findUser(String userName) {
        Optional<DigiUser> digiUser =digiUserRepository.findByUserName(userName);
        if(digiUser.isPresent()){
            return digiUser.get();
        }
        return null;
    }

    @Override
    public boolean saveUser(RegisterUser registerUser) {
        DigiUser digiUser = new DigiUser(registerUser.getFirstName(),registerUser.getLastName(),
                registerUser.getUserName(),passwordEncoder.encode(registerUser.getPassword()),
                registerUser.getEmail(), registerUser.getMobile());
        long roleId = Long.parseLong(registerUser.getRoleId());
        Optional<DigiUserRole> userRole = digiUserRoleRepository.findById(roleId);
        if(userRole.isPresent())
        {
            digiUser.setRole(userRole.get());
            DigiUser savedUser = digiUserRepository.save(digiUser);
            if (savedUser != null)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<DigiUserRole> getUserRoles() {
        List<DigiUserRole> userRoles = new ArrayList<>();
        for(DigiUserRole userRole:digiUserRoleRepository.findAll()){
            userRoles.add(userRole);
        }
        return userRoles;
    }
}
