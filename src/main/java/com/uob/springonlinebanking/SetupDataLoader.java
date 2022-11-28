package com.uob.springonlinebanking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.uob.springonlinebanking.models.Privileges;
import com.uob.springonlinebanking.models.Roles;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.PrivilegeRepository;
import com.uob.springonlinebanking.repositories.RoleRepository;
import com.uob.springonlinebanking.repositories.UserRepository;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private boolean alreadySetup = false;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private PrivilegeRepository privilegeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // API

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (alreadySetup) {
            return;
        }

        // == create initial privileges
        final Privileges readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE");
        final Privileges writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE");
        final Privileges passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE");

        // == create initial roles
        final List<Privileges> adminPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege));
        final List<Privileges> userPrivileges = new ArrayList<>(Arrays.asList(readPrivilege, passwordPrivilege));
        final Roles adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", userPrivileges);

        // == create initial user ie admin
        createUserIfNotFound("admin", "admin", new ArrayList<>(Arrays.asList(adminRole)));

        alreadySetup = true;
    }

    @Transactional
    Privileges createPrivilegeIfNotFound(final String name) {
    	Privileges privilege = privilegeRepo.findPrivilegeByPrivilegeName(name);
        if (privilege == null) {
            privilege = new Privileges(name);
            privilege = privilegeRepo.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Roles createRoleIfNotFound(final String roleName, final Collection<Privileges> privilegeCollection) {
        Roles role = roleRepo.findRoleByRoleName(roleName);
        if (role == null) {
            role = new Roles(roleName);
        }
        role.setPrivilegesCollection(privilegeCollection);
        role = roleRepo.save(role);
        return role;
    }

    @Transactional
    Users createUserIfNotFound(final String userName, final String password, final Collection<Roles> roleCollection) {
        Users user = userRepo.getUserByUsername(userName);
        if (user == null) {
            user = new Users();
            user.setPassword(passwordEncoder.encode(password));
        }
        user.setUserName(userName);
        user.setRolesCollection(roleCollection);
        user = userRepo.save(user);
        return user;
    }
}
