package com.coronosafe.approval;

import com.coronosafe.approval.jdbc.DigiUserRepository;
import com.coronosafe.approval.jdbc.DigiUserRoleRepository;
import com.coronosafe.approval.jdbc.data.DigiUser;
import com.coronosafe.approval.jdbc.data.DigiUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class DigitalApprovalApplication{

    private static final Logger log = LoggerFactory.getLogger(DigitalApprovalApplication.class);

    public static void main(String[] args){
        SpringApplication.run(DigitalApprovalApplication.class);
    }

    @Bean
    public CommandLineRunner demo(DigiUserRepository repository, DigiUserRoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        return (args) -> {
            DigiUserRole role1 = new DigiUserRole(8000,"Institution");
            DigiUserRole role2 = new DigiUserRole(8100,"Mission Director");
            DigiUserRole role3 = new DigiUserRole(8200,"Government Official");
            DigiUserRole role = new DigiUserRole(8300,"TEST");

            roleRepository.save(role);
            roleRepository.save(role1);
            roleRepository.save(role2);
            roleRepository.save(role3);

            DigiUser user1 = new DigiUser(1000,"inst1_first", "inst1_last", "inst1_user", passwordEncoder.encode("testInst1"));
            DigiUser user2 = new DigiUser(1001,"director_first", "director_last", "director_user", passwordEncoder.encode("testDirector"));
            DigiUser user3 = new DigiUser(1002,"officer_first", "officer_last", "officer_user", passwordEncoder.encode("testOfficer"));
            DigiUser user = new DigiUser(1003,"test","last","testUser",passwordEncoder.encode("testUser"));


            user.setRole(role);
            user1.setRole(role1);
            user2.setRole(role2);
            user3.setRole(role3);
            repository.save(user);
            repository.save(user1);
            repository.save(user2);
            repository.save(user3);


        };
    }
}
