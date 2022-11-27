package com.uob.springonlinebanking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Roles;

public interface RoleRepository extends CrudRepository<Roles, Long> {

	@Query("SELECT r FROM Roles r WHERE r.roleName=?1")
    public Roles findRoleByRoleName(String roleName);

    @Override
    void delete(Roles role);
}
