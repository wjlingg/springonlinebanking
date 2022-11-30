package com.uob.springonlinebanking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Accounts;

public interface AccountRepository extends CrudRepository<Accounts, Long> {
	
	@Query("SELECT a FROM Accounts a WHERE a.accountId = ?1")
	public Accounts findByAccountId(Long accId);
	
	@Query("SELECT a FROM Accounts a WHERE a.user.userId=?1")
	public List<Accounts> getAllAccountByUserId(Long id);
}
