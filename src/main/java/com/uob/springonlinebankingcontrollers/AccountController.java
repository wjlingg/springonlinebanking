package com.uob.springonlinebankingcontrollers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.uob.springonlinebanking.repositories.AccountRepository;

@Controller
public class AccountController {
	
	@Autowired
	AccountRepository accountRepo;
	
	@GetMapping("/")
	public String showMain(Model model) {
		model.addAttribute("accounts", accountRepo.findAll()); // select * from users
		long count = accountRepo.count(); // select count(*) from users
		model.addAttribute("count", count);
		return "index";
	}
}
