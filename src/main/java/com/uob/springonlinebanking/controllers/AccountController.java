package com.uob.springonlinebanking.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
import com.uob.springonlinebanking.repositories.RoleRepository;
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
	RoleRepository roleRepo;
	@Autowired
	TransactionRepository transactionRepo;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/")
	public String showMain() {
		return "index";
	}

	// ============================================= Register Page
	@GetMapping("/register") // used in index.html
	public String showRegistrationForm(HttpServletRequest request, Model model) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			model.addAttribute("msg", (String) flashMap.get("msg"));
			model.addAttribute("userNric", (String) flashMap.get("userNric"));
			model.addAttribute("contactNo", (String) flashMap.get("contactNo"));
			model.addAttribute("address", (String) flashMap.get("address"));
			model.addAttribute("email", (String) flashMap.get("email"));
			model.addAttribute("nomineeName", (String) flashMap.get("nomineeName"));
			model.addAttribute("nomineeNric", (String) flashMap.get("nomineeNric"));
		}
		return "addUser"; // render addUser.html
	}

	@PostMapping("/process_register") // used in addUser.html
	public String processRegister(@RequestParam("accountType") String accountType, Users user,
			RedirectAttributes redirectAttributes) {
		if (userRepo.getUserByUsername(user.getUserName()) != null) {
			redirectAttributes.addFlashAttribute("msg", "userNameExist");
			redirectAttributes.addFlashAttribute("userNric", user.getUserNric());
			redirectAttributes.addFlashAttribute("contactNo", user.getContactNo());
			redirectAttributes.addFlashAttribute("address", user.getAddress());
			redirectAttributes.addFlashAttribute("email", user.getEmail());
			redirectAttributes.addFlashAttribute("nomineeName", user.getNomineeName());
			redirectAttributes.addFlashAttribute("nomineeNric", user.getNomineeNric());
			return "redirect:/register";
		} else {
			if (!StringUtils.isEmpty(user.getPassword())) {
				user.setPassword(passwordEncoder.encode(user.getPassword()));
			}
			user.setRolesCollection(Arrays.asList(roleRepo.findRoleByRoleName("ROLE_USER")));
			userRepo.save(user); // save to user repository
			Users userLocal = userRepo.getUserByUserId(user.getUserId()); // get the user that has been just saved

			double interestRate; // set interest rate
			if (accountType.equalsIgnoreCase("Savings")) {
				interestRate = 0.05;
			} else if (accountType.equalsIgnoreCase("Fixed Deposit")) {
				interestRate = 0.10;
			} else {
				interestRate = 0.15;
			}
			Accounts account = new Accounts(accountType, 0.0, userLocal, false, interestRate, LocalDate.now()); // create
																												// account
			accountRepo.save(account); // save to account repository

			return "redirect:/";
		}
	}

	// ============================================= Welcome User Page
	@GetMapping("/welcomeuser") // used in components.html navbar
	public String welcomeUser(HttpServletRequest request, Model model) {
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			String txnType = (String) flashMap.get("msg");
			if (txnType.equals("deposit")) {
				model.addAttribute("balAfterDeposit", (Double) flashMap.get("balAfterDeposit"));
			} else if (txnType.equals("withdraw")) {
				model.addAttribute("balAfterWithdrawal", (Double) flashMap.get("balAfterWithdrawal"));
			}
			model.addAttribute("txnAmt", (Double) flashMap.get("txnAmt"));
			model.addAttribute("accountNo", (Long) flashMap.get("accountNo"));
			model.addAttribute("msg", txnType);
		}
		return "welcomeUser"; // render welcomeUser.html
	}

	// ============================================= Create another account

	@GetMapping("/createaccount") // used in welcomeUser.html
	public String createAccount(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
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
			@AuthenticationPrincipal MyUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		Users userExisting = userRepo.getUserByUserId(userId);

		double interestRate; // set interest rate
		if (accountType.equalsIgnoreCase("Savings")) {
			interestRate = 0.05;
		} else if (accountType.equalsIgnoreCase("Fixed Deposit")) {
			interestRate = 0.10;
		} else {
			interestRate = 0.15;
		}

		Accounts newAccount = new Accounts(accountType, 0.0, userExisting, false, interestRate, LocalDate.now()); // create
		// account
		// user

		accountRepo.save(newAccount); // save to account repository

		return "redirect:/welcomeuser";
	}

	// ============================================= View account details
	@GetMapping("/viewaccount") // used in welcomeUser.html, addAccount.html, viewAccountForm.html
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

		List<Long> optionList = new ArrayList<Long>();
		List<Accounts> accountList = user.getAccountList();
		for (Accounts account : accountList) {
			if (!account.isDormant()) { // only allow viewing on active account
				optionList.add(account.getAccountId());
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
		List<Transactions> txn = transactionRepo.getTransactionByAccountId(accId);
		redirectAttributes.addFlashAttribute("acct", acct);
		redirectAttributes.addFlashAttribute("accId", accId);
		redirectAttributes.addFlashAttribute("txn", txn);

		return "redirect:/viewaccount";
	}

	/*
	 * // ============================================= Delete account details
	 * 
	 * @GetMapping("delete_account/{accId}") public String
	 * showDeleteAccount(@PathVariable("accId") Long accId, Model model) { Accounts
	 * acct = accountRepo.findByAccountId(accId); model.addAttribute("acct", acct);
	 * model.addAttribute("accId", accId); model.addAttribute("acctInitiationDate",
	 * acct.getInitiationDate()); // account open date
	 * model.addAttribute("todayDate", LocalDate.now()); // today date
	 * model.addAttribute("acctInterestRate", acct.getInterestRate());
	 * model.addAttribute("balance", acct.getBalance()); // current balance
	 * 
	 * // calculate no. of months LocalDate today = LocalDate.now(); LocalDate
	 * openDay = acct.getInitiationDate(); double diffMonths =
	 * ChronoUnit.MONTHS.between(openDay, today); // calculate the months between
	 * openDate and
	 * 
	 * // calculate interest earned double acctInterestRate =
	 * acct.getInterestRate(); double balance = acct.getBalance(); double earnedInt
	 * = balance * acctInterestRate * diffMonths / 12.0;
	 * 
	 * double totalBalance = balance + earnedInt; model.addAttribute("earnedInt",
	 * earnedInt); model.addAttribute("totalBalance", totalBalance);
	 * 
	 * return "deleteAccount"; }
	 */

	// ============================================= Delete account details
	@GetMapping("account_actions/{accId}") // used in viewAccount.html
	public String showAccountAction(@PathVariable("accId") Long accId, Model model) {
		Accounts acct = accountRepo.findByAccountId(accId);
		model.addAttribute("acct", acct);
		model.addAttribute("accId", accId);
		model.addAttribute("acctInitiationDate", acct.getInitiationDate()); // account open date
		model.addAttribute("todayDate", LocalDate.now()); // today date
		model.addAttribute("acctInterestRate", acct.getInterestRate());
		model.addAttribute("balance", acct.getBalance()); // current balance

		// calculate no. of months
		LocalDate today = LocalDate.now();
		LocalDate openDay = acct.getInitiationDate();

		double diffMonths = ChronoUnit.MONTHS.between(openDay, today); // calculate the months between openDate and

		// calculate maturity date
		LocalDate maturityDate = ChronoUnit.YEARS.addTo(acct.getInitiationDate(), 1); // how to dd-mm-yyyy
		model.addAttribute("maturityDate", maturityDate);
		long daysMaturityDate = ChronoUnit.DAYS.between(today, maturityDate);
		model.addAttribute("daysMaturityDate", daysMaturityDate);

		// calculate interest earned
		double acctInterestRate = acct.getInterestRate();
		double balance = acct.getBalance();
		double earnedInt = getSimpleInterest(balance, acctInterestRate, 12.0, diffMonths);

//		double earnedInt = balance * acctInterestRate * diffMonths / 12.0;

		double totalBalance = balance + earnedInt;

		double totalBalanceFixed = getTotalBalanceFixed(balance, acctInterestRate, 12.0, diffMonths);
//		System.out.println(totalBalanceFixed);
		double totalBalanceRecurring = getTotalBalanceRecurring(balance, acctInterestRate, 12.0, diffMonths, 500.0);
//		System.out.println(totalBalanceRecurring);

		model.addAttribute("earnedInt", earnedInt);
		model.addAttribute("totalBalance", totalBalance);

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
//		System.out.println(numOfMonths);
		if (numOfMonths == 0.0) {
			return principal - contribution;
		}
		return getTotalBalanceRecurring(principal * (1 + interestRate / compoundedNumOfTime) + contribution,
				interestRate, compoundedNumOfTime, numOfMonths - 1, contribution);
	}

	@PutMapping("/confirm_delete_account/{accId}/{totalBalance}") // used in accountActions.html
	public String confirmDeleteAccount(@PathVariable("accId") Long accId, @PathVariable("totalBalance") Double balance,
			@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		Accounts account = accountRepo.findByAccountId(accId);
		Long accountId = account.getAccountId();
		account.setBalance(0);
		account.setDormant(true);
		Users user = userRepo.getUserByUserId(userDetails.getUserId());
		account.setUser(user);

		System.out.println(accountId);
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

//		transactionRepo.updateTxnDormantStatus(true, accountId);

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
			@PathVariable("totalBalance") Double totalBalance, @RequestParam("depositAmt") Double depositAmt,
			Model model, @AuthenticationPrincipal MyUserDetails userDetails) {

		if (depositAmt == totalBalance) {
			// TODO : write logic to save to fixed deposit account
			return "redirect:/welcomeuser";
		}

		double remainingAmt = depositAmt - totalBalance;
		boolean checkBalance = remainingAmt > 0;
		if (checkBalance) {
			remainingAmt += 500.0;
		}
		System.out.println("Amt to save: " + remainingAmt);
		model.addAttribute("msg", "getSavings");
		model.addAttribute("depositAmt", depositAmt);

		Accounts account = accountRepo.findByAccountId(accId);
		model.addAttribute("acct", account);

		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		model.addAttribute("user", user); // populate addAccount.html with current user details

		List<Accounts> accountList;
		if (checkBalance) {
			accountList = accountRepo.findByAccountDetails1(userId, remainingAmt, false, "Savings");
		} else {
			accountList = accountRepo.findByAccountDetails2(userId, false, "Savings");
		}

		List<Long> optionList = new ArrayList<Long>();
		for (Accounts accountSaving : accountList) {
			optionList.add(accountSaving.getAccountId());
		}

		model.addAttribute("optionList", optionList);
		Integer count = optionList.size();
		model.addAttribute("count", count);
		// TODO: if count is only one just process immediately
		return "renewDeposit";
	}

	@PostMapping("/process_renew_deposit_saving/{accFixedId}/{totalBalance}/{depositAmt}") // used in renewDeposit.html
	public String processRenewDepositSaving(@PathVariable("accFixedId") Long accFixedId,
			@PathVariable("totalBalance") Double totalBalance, @PathVariable("depositAmt") Double depositAmt,
			@RequestParam("accSavingsId") Long accSavingsId, Model model, @AuthenticationPrincipal MyUserDetails userDetails) {
//		model.addAttribute("depositAmt", depositAmt);
		System.out.println("Fixed acc: " + accFixedId);
		System.out.println("Total bal: " + totalBalance);
		System.out.println("Savings acc: " + accSavingsId);
		System.out.println("Deposit amt: " + depositAmt);
		
		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		
		Accounts accountSavings = accountRepo.findByAccountId(accSavingsId);
		Accounts accountFixed = accountRepo.findByAccountId(accFixedId);
		double remainingAmt = depositAmt - totalBalance;
		double txnAmt = accountSavings.getBalance() - remainingAmt;
		
		Transactions transaction = new Transactions();
		
		if (remainingAmt > 0) { // eg deposit = 7000, balance = 6655 -> 7000-6655
//			model.addAttribute("requiredAmt", depositAmt - totalBalance);
			// TODO: write logic to ask for more money from savings account
//			model.addAttribute("subMsg", "more");
			transaction.setTransactionAmount(remainingAmt);
			transaction.setTxnType("withdraw");
		} else {// eg deposit = 6000, balance = 6655 -> 6655 - 6000
//			model.addAttribute("excessAmt", totalBalance - depositAmt);
			// TODO: write logic to ask where to save the excess amount
//			model.addAttribute("subMsg", "less");
			transaction.setTransactionAmount(-1*remainingAmt);
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
		transactionRepo.save(transaction);
		return "redirect:/welcomeuser";
	}
}