package com.uob.springonlinebanking.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Transactions {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long transactionId;

	private String status; // success, failure

	private double transactionAmount;

	private LocalDateTime dateTime;

	@ManyToOne
	@JoinColumn(name = "account_transaction")
	private Accounts account;
	
	private String txnType; // deposit, withdraw
	
	private String msg; // additional message for failure

	public Transactions() {
		super();
	}

	public Transactions(long transactionId, String status, double transactionAmount, 
						LocalDateTime dateTime, Accounts account, String txnType, String msg) {
		super();
		this.transactionId = transactionId;
		this.status = status;
		this.transactionAmount = transactionAmount;
		this.dateTime = dateTime;
		this.account = account;
		this.txnType = txnType;
		this.msg = msg;
	}

	public long getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(double transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Accounts getAccount() {
		return account;
	}

	public void setAccount(Accounts account) {
		this.account = account;
	}

	public String getTxnType() {
		return txnType;
	}

	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	@Override
	public String toString() {
		return "Transactions [transactionId=" + transactionId + ", status=" + status + ", transactionAmount="
				+ transactionAmount + ", dateTime=" + dateTime + ", account=" + account + ", txnType="
				+ txnType + ", msg=" + msg + "]";
	}
}
