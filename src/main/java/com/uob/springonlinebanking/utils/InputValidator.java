package com.uob.springonlinebanking.utils;

import java.util.regex.Pattern;

public class InputValidator {

    public static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern.compile("^[0-9]{10}$");

    public static boolean isSearchCriteriaValid(AccountInput accountInput) {
        return ACCOUNT_NUMBER_PATTERN.matcher(accountInput.getAccountNumber()).find();
    }

    public static boolean isAccountNoValid(String accountNo) {
        return ACCOUNT_NUMBER_PATTERN.matcher(accountNo).find();
    }

    public static boolean isCreateAccountCriteriaValid(CreateAccountInput createAccountInput) {
        return (!createAccountInput.getUserName().isBlank() && !createAccountInput.getUserNric().isBlank());
    }
}
