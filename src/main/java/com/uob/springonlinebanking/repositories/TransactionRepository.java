package com.uob.springonlinebanking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.uob.springonlinebanking.models.Transactions;

public interface TransactionRepository extends CrudRepository<Transactions, Long> {
	
//	@Query("SELECT t FROM Transactions t WHERE t.account_transaction=?1")
//	public List<Transactions> getTransactionByAccountId(Long accountId);
}
