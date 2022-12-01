package com.uob.springonlinebanking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.uob.springonlinebanking.models.Users;

public interface UserRepository extends CrudRepository<Users, Long> {
	@Query("SELECT u FROM Users u WHERE u.userName = ?1")
	public Users getUserByUsername(String username);
	
	@Query("SELECT u FROM Users u WHERE u.userName LIKE %:value%")
	public List<Users> getSearchedUserByUsername(@Param("value")String searchString);
	
	@Query("SELECT u FROM Users u WHERE u.userId = ?1")
	public Users getUserByUserId(Long userId);
}
