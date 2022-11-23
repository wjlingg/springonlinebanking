package com.uob.springonlinebanking.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
public class Accounts {

    @Id
    @GenericGenerator(
	    name = "account-sequence-generator",
	    strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	    parameters = {
	            @Parameter(name = "sequence_name", value = "account_sequence"),
	            @Parameter(name = "initial_value", value = "1000000000"),
	            @Parameter(name = "increment_size", value = "1")
	    })
	@GeneratedValue(generator = "account-sequence-generator")
	private long accountId;
    
    private String accountType;
    
    private double balance;

    @ManyToOne
    @JoinColumn(name = "user_account")
    private Users user;
    
    @OneToMany
	@JoinColumn(name = "account_transaction")
	private List<Transactions> accountTransactionList = new ArrayList<>();    
    
	public Accounts() {
		super();
	}

	public Accounts(long accountId, String accountType, double balance, Users user,
			List<Transactions> accountTransactionList) {
		super();
		this.accountId = accountId;
		this.accountType = accountType;
		this.balance = balance;
		this.user = user;
		this.accountTransactionList = accountTransactionList;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public List<Transactions> getAccountTransactionList() {
		return accountTransactionList;
	}

	public void setAccountTransactionList(List<Transactions> accountTransactionList) {
		this.accountTransactionList = accountTransactionList;
	}

	@Override
	public String toString() {
		return "Accounts [accountId=" + accountId + ", accountType=" + accountType + ", balance=" + balance + ", user="
				+ user + ", accountTransactionList=" + accountTransactionList + "]";
	}
}	
