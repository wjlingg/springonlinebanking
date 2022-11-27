package com.uob.springonlinebanking.controllers;

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
import org.springframework.web.servlet.support.RequestContextUtils;

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

	@GetMapping("/register") // used in index.html
	public String showRegistrationForm() {

		return "addUser"; // render addUser.html
	}

	@PostMapping("/process_register") // used in addUser.html
	public String processRegister(@RequestParam("accountType") String accountType, Users user) {
		if (!StringUtils.isEmpty(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		userRepo.save(user); // save to user repository
		Users userLocal = userRepo.getUserByUserId(user.getUserId()); // get the user that has been just saved
		Accounts account = new Accounts(accountType, 0.0, userLocal); // create account with the saved user
		accountRepo.save(account); // save to account repository

		return "redirect:/";
	}

	@GetMapping("/welcomeuser") // used in components.html navbar
	public String welcomeUser() {
		return "welcomeUser"; // render welcomeUser.html
	}

	// ============================================= Create another account

	@GetMapping("/createaccount") // used in welcomeUser.html
	public String createAccount(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);
		
		model.addAttribute("user", user); // populate addAccount.html with current user details
		List<Accounts> accountList = user.getAccountList();

		model.addAttribute("accountList", accountList);
		Integer count = accountList.size();
		model.addAttribute("count", count);

		return "addAccount";
	}

	@PostMapping("/process_account_creation") // used in addAccount.html
	public String processAccount(@RequestParam("accountType") String accountType,
								@AuthenticationPrincipal MyUserDetails userDetails) {
		Long userId = userDetails.getUserId();
		Users userExisting = userRepo.getUserByUserId(userId);

		Accounts newAccount = new Accounts(accountType, 0.0, userExisting); // create account with the saved user
		accountRepo.save(newAccount); // save to account repository

		return "redirect:/welcomeuser";
	}

	// ============================================= View account details
	@GetMapping("/viewaccount") // used in welcomeUser.html, addAccount.html
	public String showAccount(HttpServletRequest request, Model model) {

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
		
		return "viewAccount";
	}
}