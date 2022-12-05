package com.uob.springonlinebanking.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.uob.springonlinebanking.models.Accounts;
import com.uob.springonlinebanking.models.Transactions;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.AccountRepository;
import com.uob.springonlinebanking.repositories.TransactionRepository;
import com.uob.springonlinebanking.repositories.UserRepository;
import com.uob.springonlinebanking.security.MyUserDetails;

@Controller
public class AccountController {

	@Autowired
	AccountRepository accountRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	TransactionRepository transactionRepo;

	// ============================================= Create another account

	@GetMapping("/createaccount") // used in welcomeUser.html
	public String createAccount(HttpServletRequest request, @AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			model.addAttribute("msg", (String) flashMap.get("msg"));
		}

		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);

		model.addAttribute("user", user); // populate addAccount.html with current user details
		List<Accounts> accountList = user.getAccountList();
		List<Accounts> optionList = new ArrayList<>();
		for (Accounts account : accountList) {
			if (!account.isDormant()) { // only allow viewing on active account
				optionList.add(account);
			}
		}

		model.addAttribute("optionList", optionList);
		Integer count = optionList.size();
		model.addAttribute("count", count);

		return "addAccount";
	}

	@PostMapping("/process_account_creation") // used in addAccount.html
	public String processAccount(@RequestParam("accountType") String accountType,
			@RequestParam("balance") double balance,
			@RequestParam(value = "recurringDeposit", required = false) String recurringDeposit,
			@AuthenticationPrincipal MyUserDetails userDetails, RedirectAttributes redirectAttributes) {
		Long userId = userDetails.getUserId();
		Users userExisting = userRepo.getUserByUserId(userId);

		double tmpRecurringDeposit;
		if (recurringDeposit == "") {
			tmpRecurringDeposit = 0.0;
		} else {
			tmpRecurringDeposit = Double.parseDouble(recurringDeposit);
		}

		double interestRate; // set interest rate
		if (accountType.equalsIgnoreCase("Savings")) {
			interestRate = 0.05;
		} else if (accountType.equalsIgnoreCase("Fixed Deposit")) {
			interestRate = 0.10;
		} else {
			interestRate = 0.15;
		}

		Long newAccId = 0L;
		if (accountType.equalsIgnoreCase("Savings") || accountType.equalsIgnoreCase("Fixed Deposit")) {
			if (balance >= 500 && tmpRecurringDeposit == 0.0) {
				Accounts newAccount = new Accounts(accountType, balance, userExisting, false, interestRate,
						LocalDate.now());
				accountRepo.save(newAccount); // save to account repository
				newAccId = newAccount.getAccountId();
			} else {
				redirectAttributes.addFlashAttribute("msg", "savingsFixedError");
				return "redirect:/createaccount";
			}
		} else if (accountType.equalsIgnoreCase("Recurring Deposit")) {

			if (balance >= 500 && tmpRecurringDeposit >= 500.0) {
				Accounts newAccount = new Accounts(accountType, balance, tmpRecurringDeposit, userExisting, false,
						interestRate, LocalDate.now());
				accountRepo.save(newAccount); // save to account repository
				newAccId = newAccount.getAccountId();
			} else {
				redirectAttributes.addFlashAttribute("msg", "recurringError");
				return "redirect:/createaccount";
			}
		}
		Accounts account = accountRepo.findByAccountId(newAccId);
		Transactions transaction = new Transactions("success", balance, LocalDateTime.now(), account, "initial deposit",
				false);
		transactionRepo.save(transaction);
		return "redirect:/welcomeuser";
	}

	// ============================================= View account details
	@GetMapping("/viewaccount") // used in welcomeUser.html, addAccount.html, viewAccount.html
	public String showAccount(HttpServletRequest request, @AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {

//		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
//		if (flashMap != null) {
//			model.addAttribute("acct", (Accounts) flashMap.get("acct"));
//			model.addAttribute("txn", (List<Transactions>) flashMap.get("txn"));
//		}
		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		model.addAttribute("user", user); // populate addAccount.html with current user details

		Map<Long, String> optionList = new HashMap<Long, String>();
		List<Accounts> accountList = user.getAccountList();
		for (Accounts account : accountList) {
			if (!account.isDormant()) { // only allow viewing on active account
				String stringValue = account.getAccountId() + " - " + account.getAccountType();
				optionList.put(account.getAccountId(), stringValue);
			}
		}

		model.addAttribute("optionList", optionList);
		Integer count = optionList.size();
		model.addAttribute("count", count);

		return "viewAccount";
	}

	@PostMapping("/process_view_account") // used in viewAccount.html
	public String processShowAccount(@RequestParam("accId") Long accId, Model model,
			RedirectAttributes redirectAttributes) {
		Accounts acct = accountRepo.findByAccountId(accId);
		List<Transactions> txn = transactionRepo.getSuccessTxnByAccountId(accId);
		redirectAttributes.addFlashAttribute("acct", acct);
		redirectAttributes.addFlashAttribute("accId", accId);
		redirectAttributes.addFlashAttribute("txn", txn);

		return "redirect:/viewaccount";
	}

	// ============================================= Delete account details
	@GetMapping("account_actions/{accId}") // used in viewAccount.html
	public String showAccountAction(@PathVariable("accId") Long accId,
			@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		Accounts acct = accountRepo.findByAccountId(accId);
		model.addAttribute("acct", acct);
		model.addAttribute("accId", accId);
		model.addAttribute("acctInitiationDate", acct.getInitiationDate()); // account open date
		model.addAttribute("todayDate", LocalDate.now()); // today date
		model.addAttribute("acctInterestRate", acct.getInterestRate());
		model.addAttribute("msg", "null");

		// calculate no. of months
		LocalDate today = LocalDate.now();
		LocalDate openDay = acct.getInitiationDate();

		double diffMonths = ChronoUnit.MONTHS.between(openDay, today); // calculate the months between openDate and todayDate

		// calculate maturity date
		LocalDate maturityDate = ChronoUnit.YEARS.addTo(acct.getInitiationDate(), 1); // how to dd-mm-yyyy
		model.addAttribute("maturityDate", maturityDate);
		long daysMaturityDate = ChronoUnit.DAYS.between(today, maturityDate);
		model.addAttribute("daysMaturityDate", daysMaturityDate);

		// calculate interest earned
		double acctInterestRate = acct.getInterestRate();
		double balance = acct.getBalance();
		double earnedInt = 0.0;
		double totalBalance = 0.0;

		switch (acct.getAccountType()) {
		case "Savings":
			earnedInt = getSimpleInterest(balance, acctInterestRate, 12.0, diffMonths);
			totalBalance = balance + earnedInt;
			break;
		case "Fixed Deposit":
			totalBalance = getTotalBalanceFixed(balance, acctInterestRate, 12.0, diffMonths);
			earnedInt = totalBalance - balance;
			break;
		case "Recurring Deposit":
			if (diffMonths == 0) {
				totalBalance = balance;
			} else {
				double balanceWithRecurringDeposit = balance + acct.getRecurringDeposit() * (diffMonths - 1);
				totalBalance = getTotalBalanceRecurring(balance, acctInterestRate, 12.0, diffMonths,
						acct.getRecurringDeposit());
				earnedInt = totalBalance - balanceWithRecurringDeposit;
			}
			break;
		}

		model.addAttribute("balance", balance);
		model.addAttribute("earnedInt", earnedInt);
		model.addAttribute("totalBalance", totalBalance);

		if (!acct.getAccountType().equals("Savings")) {
			// check if the user got savings account
			Long userId = userDetails.getUserId();
			List<Accounts> accountSavingList = accountRepo.findByAccountDetails2(userId, false, "Savings");
			Map<Long, String> optionList = new HashMap<Long, String>();
			for (Accounts account : accountSavingList) {
				String stringValue = account.getAccountId() + " ($" + account.getBalance() + ")";
				optionList.put(account.getAccountId(), stringValue);
			}

			model.addAttribute("optionList", optionList);
			Integer count = optionList.size();
			model.addAttribute("count", count);
			if (count != 0) {
				model.addAttribute("msg", "getSavings");
			}
		}
		return "accountActions";
	}

	public Double getSimpleInterest(double principal, double interestRate, double compoundedNumOfTime,
			double numOfMonths) {
		return principal * interestRate * numOfMonths / compoundedNumOfTime;
	}

	public Double getTotalBalanceFixed(double principal, double interestRate, double compoundedNumOfTime,
			double numOfMonths) {
		return principal * Math.pow((1 + interestRate / compoundedNumOfTime), numOfMonths);
	}

	public Double getTotalBalanceRecurring(double principal, double interestRate, double compoundedNumOfTime,
			double numOfMonths, double contribution) {
		if (numOfMonths == 0.0) {
			return principal - contribution;
		}
		return getTotalBalanceRecurring(principal * (1 + interestRate / compoundedNumOfTime) + contribution,
				interestRate, compoundedNumOfTime, numOfMonths - 1, contribution);
	}

	@PutMapping("/confirm_delete_account/{accId}/{totalBalance}") // used in accountActions.html
	public String confirmDeleteAccount(@PathVariable("accId") Long accId, @PathVariable("totalBalance") Double balance,
			@RequestParam(value = "accSavingsId", required = false) String accSavingsId,
			@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		Accounts account = accountRepo.findByAccountId(accId);
		System.out.println("Savings acc: " + accSavingsId);
		if (accSavingsId != null) {
			Accounts accountSavings = accountRepo.findByAccountId(Long.parseLong(accSavingsId.split("\\(")[0].trim()));
			accountSavings.setBalance(accountSavings.getBalance() + balance);
			System.out.println(accountSavings.getBalance());
			account.setBalance(0);
			account.setDormant(true);
			accountRepo.save(accountSavings);
			accountRepo.save(account);

			Transactions transactionNewSavings = new Transactions();
			transactionNewSavings.setTransactionAmount(balance);
			transactionNewSavings.setTxnType("deposit");
			transactionNewSavings.setAccount(accountSavings);
			transactionNewSavings.setDateTime(LocalDateTime.now());
			transactionNewSavings.setDormant(false);
			transactionNewSavings.setStatus("success");

			transactionRepo.save(transactionNewSavings);

			Transactions transactionNewDeposit = new Transactions();
			transactionNewDeposit.setTransactionAmount(balance);
			transactionNewDeposit.setTxnType("withdraw");
			transactionNewDeposit.setAccount(account);
			transactionNewDeposit.setDateTime(LocalDateTime.now());
			transactionNewDeposit.setDormant(true);
			transactionNewDeposit.setStatus("success");

			transactionRepo.save(transactionNewDeposit);

			return "welcomeUser";
		}

		Long accountId = account.getAccountId();
		account.setBalance(0);
		account.setDormant(true);
		Users user = userRepo.getUserByUserId(userDetails.getUserId());
		account.setUser(user);

		List<Transactions> txnList = transactionRepo.getTransactionByAccountId(accountId);
		account.setAccountTransactionList(txnList);
		// need to save the account before saving transactions
		// need to set the necessary variables before saving
		accountRepo.save(account);
		Accounts acct = accountRepo.findByAccountId(accountId);
		for (Transactions txn : txnList) {
			txn.setDormant(true);
			txn.setAccount(acct);
			transactionRepo.save(txn);
		}

		Transactions transactionNew = new Transactions();
		transactionNew.setTransactionAmount(balance);
		transactionNew.setTxnType("withdraw");
		transactionNew.setAccount(account);
		transactionNew.setDateTime(LocalDateTime.now());
		transactionNew.setDormant(true);
		transactionNew.setStatus("success");

		transactionRepo.save(transactionNew);

		return "welcomeUser";
	}

	// ====================================== Renew fixed/recurring account details
	@GetMapping("/renewDeposit/{accId}/{totalBalance}") // used in accountActions.html
	public String renewDeposit(@PathVariable("accId") Long accId, @PathVariable("totalBalance") Double totalBalance,
			Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
		System.out.println("Fixed account: " + accId);
		System.out.println("Total balance: " + totalBalance);
		Accounts account = accountRepo.findByAccountId(accId);
		model.addAttribute("acct", account);
		model.addAttribute("accId", accId);
		model.addAttribute("totalBalance", totalBalance);
		model.addAttribute("msg", "null");

		return "renewDeposit";
	}

	@PostMapping("/process_renew_deposit/{accId}/{totalBalance}") // used in renewDeposit.html
	public String processRenewDeposit(@PathVariable("accId") Long accId,
			@PathVariable("totalBalance") Double totalBalance, @RequestParam("depositAmtInput") Double depositAmtInput,
			@RequestParam(value = "depositAmtChecked", required = false) String depositAmtChecked,
			@RequestParam(value = "depositAmtCheckedConfirm", required = false) String depositAmtCheckedConfirm,
			Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
		Accounts account = accountRepo.findByAccountId(accId);
		System.out.println("depositAmtInput: " + depositAmtInput);
		System.out.println("depositAmtChecked: " + depositAmtChecked);
		model.addAttribute("acct", account);

		if (depositAmtChecked != null) { // if full transfer of deposit amount is checked
			if (depositAmtInput != 0) { // if manual input also checked ask user key in again
				System.out.println("multiple selected amount");
				model.addAttribute("accId", accId);
				model.addAttribute("totalBalance", totalBalance);
				model.addAttribute("msg", "null");
				model.addAttribute("msgSelect", "multipleSelection");
				return "renewDeposit";
			}
			// else ask user to confirm full deposit selection
			System.out.println("full deposit amount");
			model.addAttribute("depositAmtChecked", depositAmtChecked);
			model.addAttribute("msg", "fullDeposit");
			return "renewDeposit";
		}

		if (depositAmtCheckedConfirm != null) { // if confirm by user then proceed with setting of balance
			System.out.println("full deposit amount confirmed");
			account.setBalance(totalBalance);
			account.setInitiationDate(LocalDate.now());
			accountRepo.save(account);
			Accounts accountNew = accountRepo.findByAccountId(accId);
			Transactions transaction = new Transactions("success", totalBalance, LocalDateTime.now(), accountNew,
					"initial deposit", false);
			transactionRepo.save(transaction);
			return "redirect:/welcomeuser";
		}

		if (depositAmtInput == 0) { // if never input deposit amount redirect back
			System.out.println("zero deposit amount");
			model.addAttribute("accId", accId);
			model.addAttribute("totalBalance", totalBalance);
			model.addAttribute("msg", "null");
			model.addAttribute("msgSelect", "noSelection");
			return "renewDeposit";
		}

		if (depositAmtInput < 500) { // if input deposit amount is less than $500 redirect back
			System.out.println("minimum $500 deposit amount needed");
			model.addAttribute("accId", accId);
			model.addAttribute("totalBalance", totalBalance);
			model.addAttribute("msg", "null");
			model.addAttribute("msgSelect", "minSelection");
			return "renewDeposit";
		}

		double remainingAmt = depositAmtInput - totalBalance;
		boolean checkBalance = remainingAmt > 0; // if deposit more than total balance
		if (checkBalance) { // then add $500 because savings account need to be at least $500 remaining
							// after deduction
			remainingAmt += 500.0;
			model.addAttribute("msgSub", "withdraw");
			model.addAttribute("remainingAmt", remainingAmt - 500);
		} else {
			model.addAttribute("msgSub", "deposit");
			model.addAttribute("remainingAmt", -1 * remainingAmt);
		}

		System.out.println("Amt to save: " + remainingAmt);
		model.addAttribute("msg", "getSavings");
		model.addAttribute("depositAmtInput", depositAmtInput);

		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		model.addAttribute("user", user); // populate addAccount.html with current user details

		List<Accounts> accountList;
		List<Accounts> accountTempList;
		if (checkBalance) { // if deposit is more than total balance, need withdraw from savings account
			accountTempList = accountRepo.findByAccountDetails2(userId, false, "Savings");
			System.out.println("temp list size: " + accountTempList.size());

			accountList = accountRepo.findByAccountDetails1(userId, remainingAmt, false, "Savings");
			if (accountTempList.size() == 0) { // if there is no savings account then ask user to create account
				model.addAttribute("countMsg", "empty");
			} else { // if there are savings account but not enough funds then ask user to add
						// deposit
				model.addAttribute("countMsg", "insufficient");
			}
		} else { // if deposit is less than total balance, need deposit to savings account

			accountList = accountRepo.findByAccountDetails2(userId, false, "Savings");
			if (accountList.size() == 0) { // if there is no savings account then ask user to create account
				model.addAttribute("countMsg", "empty");
			} else { // if there are savings account
				model.addAttribute("countMsg", "enough");
			}
		}
		Map<Long, String> optionList = new HashMap<Long, String>();
		for (Accounts accountSaving : accountList) {
			String stringValue = accountSaving.getAccountId() + " ($" + accountSaving.getBalance() + ")";
			optionList.put(accountSaving.getAccountId(), stringValue);
		}

		model.addAttribute("optionList", optionList);
		Integer count = optionList.size();
		model.addAttribute("count", count);
		return "renewDeposit";
	}

	@PostMapping("/process_renew_deposit_saving/{accFixedId}/{totalBalance}/{depositAmt}") // used in renewDeposit.html
	public String processRenewDepositSaving(@PathVariable("accFixedId") Long accFixedId,
			@PathVariable("totalBalance") Double totalBalance, @PathVariable("depositAmt") Double depositAmt,
			@RequestParam("accSavingsId") String accSavingsId, Model model,
			@AuthenticationPrincipal MyUserDetails userDetails) {

		System.out.println("Fixed acc: " + accFixedId);
		System.out.println("Total bal: " + totalBalance);
		System.out.println("Savings acc: " + accSavingsId);
		System.out.println("Deposit amt: " + depositAmt);

		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);

		Accounts accountSavings = accountRepo.findByAccountId(Long.parseLong(accSavingsId.split("\\(")[0].trim()));
		System.out.println("Savings accId after: " + accountSavings.getAccountId());
		Accounts accountFixed = accountRepo.findByAccountId(accFixedId);
		double remainingAmt = depositAmt - totalBalance;
		double txnAmt = accountSavings.getBalance() - remainingAmt;

		Transactions transaction = new Transactions();

		if (remainingAmt > 0) { // eg deposit = 7000, balance = 6655 -> 7000-6655
			transaction.setTransactionAmount(remainingAmt);
			transaction.setTxnType("withdraw");
		} else {// eg deposit = 6000, balance = 6655 -> 6655 - 6000
			transaction.setTransactionAmount(-1 * remainingAmt);
			transaction.setTxnType("deposit");
		}

		accountSavings.setBalance(txnAmt);
		System.out.println("After setting balance: " + accountSavings.getBalance());
		accountRepo.save(accountSavings);

		accountFixed.setBalance(depositAmt);
		accountFixed.setInitiationDate(LocalDate.now());
		accountRepo.save(accountFixed);

		transaction.setAccount(accountSavings);
		transaction.setDateTime(LocalDateTime.now());
		transaction.setDormant(false);
		transaction.setStatus("success");
		transactionRepo.save(transaction);

		return "redirect:/welcomeuser";
	}
}