package com.uob.springonlinebanking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.uob.springonlinebanking.models.Accounts;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.AccountRepository;
import com.uob.springonlinebanking.repositories.UserRepository;

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
		if(!StringUtils.isEmpty(user.getPassword())) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}
		userRepo.save(user); // save to user repository
		Users userLocal = userRepo.findUserIdByNric(user.getUserNric()); // get the user that has been just saved
		Accounts account = new Accounts(accountType, 0.0, userLocal); // create account with the saved user
		accountRepo.save(account); // save to account repository

		return "redirect:/";
	}
	
	
	@GetMapping("/welcomeuser")
	public String welcomeUser() {
		return "welcomeUser"; // render welcomeUser.html
	}
}