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

/*    @Bean
    public CommandLineRunner demo(DigiUserRepository repository, DigiUserRoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        return (args) -> {
            DigiUserRole role1 = new DigiUserRole("Institution");
            DigiUserRole role2 = new DigiUserRole("Mission Director");
            DigiUserRole role3 = new DigiUserRole("Government Official");

            roleRepository.save(role1);
            roleRepository.save(role2);
            roleRepository.save(role3);

            DigiUser user1 = new DigiUser("inst1_first", "inst1_last", "inst1_user", passwordEncoder.encode("testInst1"),"inst1@test.com","9638527415");
            DigiUser user2 = new DigiUser("director_first", "director_last", "director_user", passwordEncoder.encode("testDirector"),"inst2@test.com","9876541235");
            DigiUser user3 = new DigiUser("officer_first", "officer_last", "officer_user", passwordEncoder.encode("testOfficer"),"inst3@test.com","9517534568");


             user1.setRole(role1);
            user2.setRole(role2);
            user3.setRole(role3);

             repository.save(user1);
            repository.save(user2);
            repository.save(user3);


        };
    }*/
}
