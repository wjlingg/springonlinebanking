package com.uob.springonlinebankingcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

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
	public String processRegister(Users user, Model model) {
		
		userRepo.save(user);
		model.addAttribute("username", user.getUserName()); // this is for welcomeUser.html
		
		return "welcomeUser"; // render welcomeUser.html
	}
}
