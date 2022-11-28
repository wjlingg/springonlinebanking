package com.uob.springonlinebanking.controllers;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	// ============================================= Register

	@GetMapping("/register") // used in index.html
	public String showRegistrationForm() {

		return "addUser"; // render addUser.html
	}

	@PostMapping("/process_register") // used in addUser.html
	public String processRegister(@RequestParam("accountType") String accountType, Users user) {
		if (!StringUtils.isEmpty(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		user.setRolesCollection(Arrays.asList(roleRepo.findRoleByRoleName("ROLE_USER")));
		userRepo.save(user); // save to user repository
		Users userLocal = userRepo.getUserByUserId(user.getUserId()); // get the user that has been just saved
		Accounts account = new Accounts(accountType, 0.0, userLocal, false, 0.036, LocalDate.now()); // create account
																										// with the
																										// saved user
		accountRepo.save(account); // save to account repository

		return "redirect:/";
	}

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

		Accounts newAccount = new Accounts(accountType, 0.0, userExisting, false, 0.036, LocalDate.now()); // create
																											// account
																											// with the
																											// saved
																											// user
		accountRepo.save(newAccount); // save to account repository

		return "redirect:/welcomeuser";
	}

	// ============================================= View account details
	@GetMapping("/viewaccount") // used in welcomeUser.html, addAccount.html, viewAccountForm.html
	public String showAccount(HttpServletRequest request, @AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {

		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			model.addAttribute("acct", (Accounts) flashMap.get("acct"));
			model.addAttribute("txn", (List<Transactions>) flashMap.get("txn"));
		}
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
		redirectAttributes.addFlashAttribute("acct", acct);

		List<Transactions> txn = transactionRepo.getTransactionByAccountId(accId);
		redirectAttributes.addFlashAttribute("txn", txn);
		
		return "redirect:/viewaccount";
	}

	// ============================================= Delete account details
	@PostMapping("/process_delete_account")
	public String showDeleteAccount(@RequestParam("accId") Long accId, Model model) {
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
																		// todayDate

		// calculate interest earned
		double acctInterestRate = acct.getInterestRate();
		double balance = acct.getBalance();
		double earnedInt = balance * acctInterestRate * diffMonths / 12.0;

		double totalBalance = balance + earnedInt;
		model.addAttribute("earnedInt", earnedInt);
		model.addAttribute("totalBalance", totalBalance);

		return "deleteAccount";
	}

	@GetMapping("/confirm_delete_account")
	public String confirmDeleteAccount() {
		return "welcomeUser";
	}
}