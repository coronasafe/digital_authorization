package com.coronosafe.approval.validator;

import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;

@Service
public class DigiUserValidator implements Validator
{
    @Autowired
    private DigiUserRepository digiUserRepository;

    @Override
    public boolean supports(Class<?> aClass)
    {
        return DigiUser.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors)
    {
        DigiUser digiUser = (DigiUser)o;
        Optional<DigiUser> savedUser = digiUserRepository.findById(digiUser.getId());
        if(savedUser.isPresent()){
            errors.reject("userName","User is already registered");
        }

        Optional<DigiUser> savedUserByUserName = digiUserRepository.findByUserName(digiUser.getUserName());
        if(savedUserByUserName.isPresent()){
            errors.reject("userName","UserName is already used");
        }
    }
}
