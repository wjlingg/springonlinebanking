package com.uob.springonlinebanking.repositories;

import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Users;

public interface UserRepository extends CrudRepository<Users, Long> {
	
}
