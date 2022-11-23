package com.uob.springonlinebankingcontrollers;

import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.validation.Valid;

import org.hibernate.mapping.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uob.springonlinebanking.constants.constants;
import com.uob.springonlinebanking.models.Account;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.services.AccountService;
import com.uob.springonlinebanking.utils.AccountInput;
import com.uob.springonlinebanking.utils.CreateAccountInput;
import com.uob.springonlinebanking.utils.InputValidator;

@RestController
@RequestMapping("api/v1")
public class AccountRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountRestController.class);

    private final AccountService accountService;

    @Autowired
    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

//    @PostMapping(value = "/accounts",
//            consumes = MediaType.APPLICATION_JSON_VALUE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> checkAccountBalance(
//            // TODO In the future support searching by card number in addition to sort code and account number
//            @Valid @RequestBody AccountInput accountInput) {
//        LOGGER.debug("Triggered AccountRestController.accountInput");
//
//        // Validate input
//        if (InputValidator.isSearchCriteriaValid(accountInput)) {
//            // Attempt to retrieve the account information
//            Account account = accountService.getAccount(
//                    accountInput.getSortCode(), accountInput.getAccountNumber());
//
//            // Return the account details, or warn that no account was found for given input
//            if (account == null) {
//                return new ResponseEntity<>(constants.NO_ACCOUNT_FOUND, HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>(account, HttpStatus.OK);
//            }
//        } else {
//            return new ResponseEntity<>(constants.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
//        }
//    }


    @PutMapping(value = "/accounts",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(
            @Valid @RequestBody CreateAccountInput createAccountInput) {
        LOGGER.debug("Triggered AccountRestController.createAccountInput");
        System.out.println("hello i am here before validation -> gniewognweiongwoegnr");
        
        // Validate input
        if (InputValidator.isCreateAccountCriteriaValid(createAccountInput)) {
        	Users user = new Users(createAccountInput.getUserName(), createAccountInput.getUserNric());
            // Attempt to retrieve the account information
            Account account = accountService.createAccount(
                    "savings", 0.00, user);
            System.out.println("hello i am here -> gniewognweiongwoegnr");
            
            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(constants.CREATE_ACCOUNT_FAILED, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(account, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(constants.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public LinkedHashMap<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
    	LinkedHashMap<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
