package com.uob.springonlinebanking.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class TransactionType {

	@Id
	@Column(name="type_id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String transactionType; // withdraw, deposit, transfer

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	
}
