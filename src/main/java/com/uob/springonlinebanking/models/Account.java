package com.uob.springonlinebanking.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Account {

    @Id
    @GenericGenerator(
	    name = "account-sequence-generator",
	    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	    parameters = {
	            @Parameter(name = "sequence_name", value = "account_sequence"),
	            @Parameter(name = "initial_value", value = "1000000000")
//    	            @Parameter(name = "increment_size", value = "1")
	    })
	@GeneratedValue(generator = "account-sequence-generator")
	private long accountId;

    private double currentBalance;

    private String ownerName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

	public Account(long accountId, double currentBalance, String ownerName, Users user) {
		super();
		this.accountId = accountId;
		this.currentBalance = currentBalance;
		this.ownerName = ownerName;
		this.user = user;
	}

	public Account() {
		super();
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}
}	
