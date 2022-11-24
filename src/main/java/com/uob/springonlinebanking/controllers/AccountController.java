package com.uob.springonlinebanking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	@GetMapping("/")
	public String showMain() {
		return "index";
	}
	
	// ============================================= Register
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("users", new Users());
		
		return "addUser"; // render addUser.html
	}
	
	@PostMapping("/process_register")
	public String processRegister(@RequestParam("accountType") String accountType, Users user, Model model) {
		
		userRepo.save(user); // save to user repository
		Users userLocal = userRepo.findUserIdByNric(user.getUserNric()); // get the user that has been just saved
		Accounts account = new Accounts(accountType, 0.0, userLocal); // create account with the saved user
		accountRepo.save(account); // save to account repository
		
		model.addAttribute("username", user.getUserName()); // this is for welcomeUser.html
		
		return "welcomeUser"; // render welcomeUser.html
	}
}
