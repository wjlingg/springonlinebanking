package com.uob.springonlinebanking.controllers;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.uob.springonlinebanking.models.Accounts;
import com.uob.springonlinebanking.models.Transactions;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.AccountRepository;
import com.uob.springonlinebanking.repositories.TransactionRepository;

@Controller
public class TransactionController {
	
	@Autowired
	TransactionRepository transactionRepo;
	@Autowired
	AccountRepository accountRepo;
	
	// ============================================= View Transactions
	
//	@GetMapping("/viewtransaction")
//	public String showTransaction(@RequestParam("accountId") Long accountId, Model model) {
//		model.addAttribute("transaction", transactionRepo.getTransactionByAccountId(accountId)); // select * from customer
//		long count = transactionRepo.count(); // select count(*) from customer
//		model.addAttribute("count", count);
//		return "viewTransaction";
//	}
	
	// ============================================= Add Transactions
	@GetMapping("/addtransaction")
	public String showAddTransactionForm(Model model) {
		model.addAttribute("transactions", new Transactions());
		
		return "addTransaction"; // render addTransaction.html
	}
	
	@PostMapping("/process_transaction")
	public String doTransact(@RequestParam("accId") Long accId, @RequestParam("tType") String tType,
							 @RequestParam("transAmount") Double transAmt, Model model) {
		
		Accounts acct = accountRepo.findByAccountId(accId);
		
		Transactions transaction = new Transactions();
		transaction.setTransactionAmount(transAmt);
		transaction.setStatus("success");
		transaction.setAccount(acct);
		transaction.setDateTime(LocalDateTime.now());
		transactionRepo.save(transaction);
		
		Double currBal = acct.getBalance();
		
		if (tType.equals("deposit")) {
			acct.setBalance(currBal + transAmt);
		} else {
			acct.setBalance(currBal - transAmt);
		}
		
		accountRepo.save(acct);
		
		model.addAttribute("username", acct.getUser().getUserName());

		return "welcomeUser";
	}
}
