package com.uob.springonlinebanking.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uob.springonlinebanking.repositories.AccountRepository;

@Service
public class AccountService {

    @Autowired 
    AccountRepository accountRepository;
}
