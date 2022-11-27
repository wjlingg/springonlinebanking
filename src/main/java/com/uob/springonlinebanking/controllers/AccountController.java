package com.uob.springonlinebanking.controllers;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uob.springonlinebanking.models.Accounts;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.AccountRepository;
import com.uob.springonlinebanking.repositories.UserRepository;
import com.uob.springonlinebanking.security.MyUserDetails;

@Controller
public class AccountController {
	@Autowired
	AccountRepository accountRepo;
	@Autowired
	UserRepository userRepo;

	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@GetMapping("/")
	public String showMain() {
		return "index";
	}

	// ============================================= Register

	@GetMapping("/register")
	public String showRegistrationForm() {

		return "addUser"; // render addUser.html
	}

	@PostMapping("/process_register")
	public String processRegister(@RequestParam("accountType") String accountType, Users user) {
		if (!StringUtils.isEmpty(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		userRepo.save(user); // save to user repository
		Users userLocal = userRepo.getUserByUserId(user.getUserId()); // get the user that has been just saved
		// Accounts account = new Accounts(accountType, 0.0, userLocal); // create
		// account with the saved user

		Accounts account = new Accounts();
		account.setAccountType(accountType);
		account.setBalance(0.0);
		account.setUser(userLocal);
		account.setInterestRate(0.036);
		account.setDate1(LocalDate.now());

		accountRepo.save(account); // save to account repository

		return "redirect:/";
	}

	@GetMapping("/welcomeuser")
	public String welcomeUser() {
		return "welcomeUser"; // render welcomeUser.html
	}

	// ============================================= Create another account

	@GetMapping("/createaccount")
	public String createAccount(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		model.addAttribute("user", user);
//		model.addAttribute("userAccountList", user.getAccountList());
//		List<Long> optionList = new ArrayList<Long>();
		List<Accounts> accountList = user.getAccountList();
//		for (Accounts account : accountList) {
//			optionList.add(account.getAccountId());
//		}

		model.addAttribute("accountList", accountList);
		Integer count = accountList.size();
		System.out.println(count);
		model.addAttribute("count", count);

		return "addAccount";
	}

	@PutMapping("/process_account_creation")
	public String processAccount(@Valid Users user, @AuthenticationPrincipal MyUserDetails userDetails) {
		if (!StringUtils.isEmpty(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		Long userId = userDetails.getUserId();
		Users userExisting = userRepo.getUserByUserId(userId);
//		userExisting.setUserNric(null);
//		userExisting.setUserName(null);
//		userExisting.setPassword(null);
//		userExisting.setContactNo(null);
//		userExisting.setAddress(null);
//		userExisting.setEmail(null);
//		userExisting.setNomineeName(null);
//		userExisting.setNomineeNric(null);
		List<Accounts> existingAccountList = userExisting.getAccountList();
		List<Accounts> newAccountList = user.getAccountList();
		for (Accounts account : existingAccountList) {
			newAccountList.add(account);
		}
		user.setAccountList(newAccountList);
		userRepo.save(user);

		return "redirect:/welcomeuser";
	}

	// ============================================= View account details

	// list all Account table
	@GetMapping("/viewaccount1")
	public String viewAccount1(Accounts account, Model model, Principal principal) {
		// List<Accounts> accountList = (List<Accounts>) accountRepo.findAll(); 
		// List<Accounts> accountList2 = (List<Accounts>)
		// accountRepo.findById(accountId);
		// model.addAttribute("accountList2", accountList2);

		return "viewAccount1";
	}

	// list each Account detail (how to retrieve account detail?)
	@GetMapping("/viewaccount")
	public String viewAccount(HttpServletRequest request, @AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {
		
		model.addAttribute("accounts", new Accounts());
		
		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		//Accounts account = accountRepo.findByAccountId(accId);

		System.out.println("test");	
				
		return "viewAccount";
	}
	
	//not working yet
	@RequestMapping("/deleteaccount")
	public String deleteAccount(
			
			@ModelAttribute("date1") Integer date1, 
			@ModelAttribute("interestRate") Double interestRate,
			@ModelAttribute("balance") Integer balance,
			@ModelAttribute("opr") String opr,
			Model model) {
		if (opr.equalsIgnoreCase("mul")) {
			double res = date1 * interestRate * balance;
			model.addAttribute("res", res);
		}
		return "deleteAccount";
	}
}
