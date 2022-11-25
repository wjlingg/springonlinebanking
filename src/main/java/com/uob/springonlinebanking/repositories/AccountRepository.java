package com.uob.springonlinebanking.repositories;

import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Accounts;

public interface AccountRepository extends CrudRepository<Accounts, Long> {

	public Accounts findByAccountId(Long accId);
}
