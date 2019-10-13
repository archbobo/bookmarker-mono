package com.spring2go.bookmarker;

import com.spring2go.bookmarker.constant.Constants;
import com.spring2go.bookmarker.model.Role;
import com.spring2go.bookmarker.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("default")
@Slf4j
public class DbSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DbSeeder(RoleRepository repository) {
        this.roleRepository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Running db seeding...");
        Role userRole = new Role();
        userRole.setName(Constants.ROLE_USER);
        roleRepository.insert(userRole);

        Role adminRole = new Role();
        adminRole.setName(Constants.ROLE_ADMIN);
        roleRepository.insert(adminRole);
        log.info("Db seeded.");
    }
}
