package com.uob.springonlinebanking.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class Users {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long userId;
	private String userNric;
	private String userName;
	private String password;
	private String contactNo;
	private String address;
	private String email;
	private String nomineeName;
	private String nomineeNric;
	
	// userId is being referenced by Accounts entity
	@OneToMany // one user can have many accounts
	@JoinColumn(name = "user_account")
	private List<Accounts> accountList = new ArrayList<>();

	// actual physical mapping on the owning side users class, owns the foreign key roleId
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", 
			    joinColumns = @JoinColumn(name = "user_id_users", referencedColumnName = "userId"), 
			    inverseJoinColumns = @JoinColumn(name = "role_id_users", referencedColumnName = "roleId"))
    private Collection<Roles> rolesCollection;

	public Users() {
		super();
	}

	public Users(Long userId, String userNric, String userName, String password, String contactNo, String address,
			String email, String nomineeName, String nomineeNric, List<Accounts> accountList) {
		super();
		this.userId = userId;
		this.userNric = userNric;
		this.userName = userName;
		this.password = password;
		this.contactNo = contactNo;
		this.address = address;
		this.email = email;
		this.nomineeName = nomineeName;
		this.nomineeNric = nomineeNric;
		this.accountList = accountList;
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

	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public List<Accounts> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Accounts> accountList) {
		this.accountList = accountList;
	}

	public Collection<Roles> getRolesCollection() {
		return rolesCollection;
	}

	public void setRolesCollection(Collection<Roles> rolesCollection) {
		this.rolesCollection = rolesCollection;
	}
	
	@Override
	public String toString() {
		return "Users [userId=" + userId + ", userNric=" + userNric + ", userName=" + userName + ", password="
				+ password + ", contactNo=" + contactNo + ", address=" + address + ", email=" + email + ", nomineeName="
				+ nomineeName + ", nomineeNric=" + nomineeNric + ", accountList=" + accountList + "]";
	}
}
