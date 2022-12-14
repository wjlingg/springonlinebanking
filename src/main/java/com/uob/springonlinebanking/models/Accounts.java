package com.uob.springonlinebanking.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class Accounts {
	@Id
	@GeneratedValue(generator = "id_seq", strategy = GenerationType.IDENTITY)
	@SequenceGenerator(name="id_seq", sequenceName="id_sequence", initialValue=1000000000, allocationSize=1)
	private long accountId;
    
    private String accountType;
    
    private double balance;
    
    private double recurringDeposit;

    // actual physical mapping on the owning side, owns the foreign key userId
	@ManyToOne
    @JoinColumn(name = "user_account")
    private Users user;
    
	// accountId is being referenced by Transactions entity
    @OneToMany
	@JoinColumn(name = "account_transaction")
	private List<Transactions> accountTransactionList = new ArrayList<>();  
    
    private boolean isDormant;
    
    private double interestRate;
    
    private LocalDate initiationDate;
    
	public Accounts() {
		super();
	}
	
	public Accounts(String accountType, double balance, double recurringDeposit, Users user, boolean isDormant,
			double interestRate, LocalDate initiationDate) {
		super();
		this.accountType = accountType;
		this.balance = balance;
		this.recurringDeposit = recurringDeposit;
		this.user = user;
		this.isDormant = isDormant;
		this.interestRate = interestRate;
		this.initiationDate = initiationDate;
	}

	public Accounts(String accountType, double balance, Users user, 
			boolean isDormant, double interestRate, LocalDate initiationDate) {
		super();
		this.accountType = accountType;
		this.balance = balance;
		this.user = user;
		this.isDormant = isDormant;
		this.interestRate = interestRate;
		this.initiationDate = initiationDate;
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
	
	public double getRecurringDeposit() {
		return recurringDeposit;
	}

	public void setRecurringDeposit(double recurringDeposit) {
		this.recurringDeposit = recurringDeposit;
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

	public boolean isDormant() {
		return isDormant;
	}

	public void setDormant(boolean isDormant) {
		this.isDormant = isDormant;
	}

	public double getInterestRate() {
		return interestRate;
	}

	public void setInterestRate(double interestRate) {
		this.interestRate = interestRate;
	}

	public LocalDate getInitiationDate() {
		return initiationDate;
	}

	public void setInitiationDate(LocalDate initiationDate) {
		this.initiationDate = initiationDate;
	}

	@Override
	public String toString() {
		return "Accounts [accountId=" + accountId + ", accountType=" + accountType + ", balance=" + balance + ", user="
				+ user + ", accountTransactionList=" + accountTransactionList + ", isDormant=" + isDormant
				+ ", interestRate=" + interestRate + ", initiationDate=" + initiationDate + "]";
	}
}
