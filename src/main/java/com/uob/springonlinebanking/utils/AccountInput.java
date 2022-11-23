package com.uob.springonlinebanking.utils;

import java.util.Objects;

import javax.validation.constraints.NotBlank;

public class AccountInput {

    @NotBlank(message = "Account number is mandatory")
    private String accountNumber;

    public AccountInput() {}

    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Override
    public String toString() {
        return "AccountInput{" +
                "accountNumber='" + accountNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountInput that = (AccountInput) o;
        return Objects.equals(accountNumber, that.accountNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountNumber);
    }
}
