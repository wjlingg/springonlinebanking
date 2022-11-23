package com.uob.springonlinebanking.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uob.springonlinebanking.models.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
}
