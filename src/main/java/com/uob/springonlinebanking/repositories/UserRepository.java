package com.uob.springonlinebanking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Users;

public interface UserRepository extends CrudRepository<Users, Long> {
	@Query("SELECT u FROM Users u WHERE u.userName = ?1")
	public Users getUserByUsername(String username);
	
	@Query("SELECT u FROM Users u WHERE u.userId = ?1")
	public Users getUserByUserId(Long userId);
}
