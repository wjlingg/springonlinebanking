package com.uob.springonlinebanking.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.uob.springonlinebanking.models.Transactions;

public interface TransactionRepository extends CrudRepository<Transactions, Long> {

	@Query("SELECT t FROM Transactions t WHERE t.account.accountId=?1")
	public List<Transactions> getTransactionByAccountId(Long accountId);
	
	@Query("SELECT t FROM Transactions t WHERE t.account.accountId=?1 AND t.status='success'")
	public List<Transactions> getSuccessTxnByAccountId(Long accountId);
	
	@Modifying
	@Query("UPDATE Transactions t SET t.isDormant=:isDormant WHERE t.account.accountId=:accId")
//	@Query(value="UPDATE Transactions SET is_dormant=:isDormant WHERE account_transaction=:accId", nativeQuery=true)
	public void updateTxnDormantStatus(@Param("isDormant")boolean isDormant, @Param("accId")Long accId);
}
