package com.coronosafe.approval.auth;

import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.data.DigiUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    @Autowired
    DigiUserRepository digiUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        log.info("Loading user details");
        DigiUser user = digiUserRepository.findByUserName(userName);
        if(user!=null){
            log.info("Found the user "+userName);
            return User.withUsername(userName).password(user.getPassword()).roles(user.getRole().getRoleName()).build();
        }else{
            log.info("No user with this userName");
            throw new UsernameNotFoundException(userName);
        }
    }
}
