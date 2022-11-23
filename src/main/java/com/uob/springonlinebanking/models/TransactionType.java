package com.uob.springonlinebanking.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Entity
public class TransactionType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long transactionTypeId;
	
	private String transactionName; // withdraw, deposit, transfer
	
	@OneToMany
    @JoinColumn(name = "transaction_TransactionType")
	private List<Transactions> transactTransactionTypeList = new ArrayList<>();    
	
	public TransactionType() {
		super();
	}

	public TransactionType(Long transactionTypeId, String transactionName,
			List<Transactions> transactTransactionTypeList) {
		super();
		this.transactionTypeId = transactionTypeId;
		this.transactionName = transactionName;
		this.transactTransactionTypeList = transactTransactionTypeList;
	}

	public Long getTransactionTypeId() {
		return transactionTypeId;
	}

	public void setTransactionTypeId(Long transactionTypeId) {
		this.transactionTypeId = transactionTypeId;
	}

	public String getTransactionName() {
		return transactionName;
	}

	public void setTransactionName(String transactionName) {
		this.transactionName = transactionName;
	}

	public List<Transactions> getTransactTransactionTypeList() {
		return transactTransactionTypeList;
	}

	public void setTransactTransactionTypeList(List<Transactions> transactTransactionTypeList) {
		this.transactTransactionTypeList = transactTransactionTypeList;
	}

	@Override
	public String toString() {
		return "TransactionType [transactionTypeId=" + transactionTypeId + ", transactionName=" + transactionName
				+ ", transactTransactionTypeList=" + transactTransactionTypeList + "]";
	}
}
