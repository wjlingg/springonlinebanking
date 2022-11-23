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
public class Users {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	private String userNric;
	private String userName;
	private String contactNo;
	private String address;
	private String email;
	private String nomineeName;
	private String nomineeNric;
	
	@OneToMany
	@JoinColumn(name = "account_list")
	private List<Account> accounts = new ArrayList<>();

	public Users() {
		super();
	}

	public Users(Long userId, String userNric, String userName, String contactNo, String address, String email,
			String nomineeName, String nomineeNric, List<Account> accounts) {
		super();
		this.userId = userId;
		this.userNric = userNric;
		this.userName = userName;
		this.contactNo = contactNo;
		this.address = address;
		this.email = email;
		this.nomineeName = nomineeName;
		this.nomineeNric = nomineeNric;
		this.accounts = accounts;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserNric() {
		return userNric;
	}

	public void setUserNric(String userNric) {
		this.userNric = userNric;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNomineeName() {
		return nomineeName;
	}

	public void setNomineeName(String nomineeName) {
		this.nomineeName = nomineeName;
	}

	public String getNomineeNric() {
		return nomineeNric;
	}

	public void setNomineeNric(String nomineeNric) {
		this.nomineeNric = nomineeNric;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
}
