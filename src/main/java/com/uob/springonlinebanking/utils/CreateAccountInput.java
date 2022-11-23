package com.uob.springonlinebanking.utils;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public class CreateAccountInput {

    @NotBlank(message = "User name is mandatory")
    private String userName;

    @NotBlank(message = "User NRIC is mandatory")
    private String userNric;

    
    public CreateAccountInput() {}

	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserNric() {
		return userNric;
	}


	public void setUserNric(String userNric) {
		this.userNric = userNric;
	}


	@Override
	public String toString() {
		return "CreateAccountInput [userName=" + userName + ", userNric=" + userNric + "]";
	}
}
