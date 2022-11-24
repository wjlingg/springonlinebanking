package com.uob.springonlinebanking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uob.springonlinebanking.models.Transactions;
import com.uob.springonlinebanking.repositories.TransactionRepository;

@Controller
public class TransactionController {
	
	@Autowired
	TransactionRepository transactionRepo;

	// ============================================= View Transactions

	@GetMapping("/viewtransaction")
	public String showTransaction(@RequestParam("accountId") Long accountId, Model model) {
		model.addAttribute("transaction", transactionRepo.getTransactionByAccountId(accountId)); // select * from customer
		long count = transactionRepo.count(); // select count(*) from customer
		model.addAttribute("count", count);
		return "viewTransaction";
	}
	
	// ============================================= Add Transactions
	@GetMapping("/addtransaction")
	public String showRegistrationForm(Model model) {
		model.addAttribute("transactions", new Transactions());
		
		return "addTransaction"; // render addTransaction.html
	}
	
	@PostMapping("/process_transaction")
	public String addTransaction(Transactions transaction) {
		
		transactionRepo.save(transaction); // save to transaction repository
		return "redirect:/process_register"; // redirect to where??
	}

}
