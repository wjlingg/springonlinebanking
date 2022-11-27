package com.uob.springonlinebanking.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Accounts;

public interface AccountRepository extends CrudRepository<Accounts, Long> {
	
	@Query("SELECT a FROM Accounts a WHERE a.accountId = ?1")
	public Accounts findByAccountId(Long accId);
}
