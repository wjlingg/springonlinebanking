package com.uob.springonlinebanking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Privileges;

public interface PrivilegeRepository extends CrudRepository<Privileges, Long> {

    @Query("SELECT p FROM Privileges p WHERE p.privilegeName=?1")
    public Privileges findPrivilegeByPrivilegeName(String privilegeName);

    @Override
    void delete(Privileges privilege);
}
