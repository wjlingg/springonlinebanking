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
    @JoinColumn(name = "account_list")
    private Users user;

	public Accounts() {
		super();
	}

	public Accounts(String accountType, double balance, Users user) {
		this.accountType = accountType;
		this.balance = balance;
		this.user = user;
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

	@Override
	public String toString() {
		return "Account [accountId=" + accountId + ", accountType=" + accountType + ", balance=" + balance + ", user="
				+ user + "]";
	}
}	
