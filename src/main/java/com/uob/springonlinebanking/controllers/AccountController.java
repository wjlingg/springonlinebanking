package com.uob.springonlinebanking.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

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
	public String showRegistrationForm() {
				
		return "addUser"; // render addUser.html
	}
	
	@PostMapping("/process_register")
	public String processRegister(@RequestParam("accountType") String accountType, Users user, Model model, RedirectAttributes redirectAttributes) {
		
		userRepo.save(user); // save to user repository
		Users userLocal = userRepo.findUserIdByNric(user.getUserNric()); // get the user that has been just saved
		Accounts account = new Accounts(accountType, 0.0, userLocal); // create account with the saved user
		accountRepo.save(account); // save to account repository
//		model.addAttribute("username", user.getUserName()); // this is for welcomeUser.html
		redirectAttributes.addFlashAttribute("username", user.getUserName()); // redirect registered username
		return "redirect:/welcomeuser"; 
//		return "welcomeUser";
	}
	
	// link to login page
	
	@GetMapping("/welcomeuser")
	public String welcomeUser(HttpServletRequest request, Users user, Model model) {
		
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        String username =  (String) flashMap.get("username");
//		model.addAttribute("username", user.getUserName()); // this is for welcomeUser.html
		model.addAttribute("username", username);
		return "welcomeUser"; // render welcomeUser.html
	}
}