package com.uob.springonlinebanking.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uob.springonlinebanking.models.Account;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.AccountRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public Account createAccount(String accountType, double balance, Users user) {
        Account newAccount = new Account(accountType, 0.00, user);
        return accountRepository.save(newAccount);
    }
}
