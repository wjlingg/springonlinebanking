package com.uob.springonlinebanking.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.uob.springonlinebanking.models.Accounts;
import com.uob.springonlinebanking.models.Transactions;
import com.uob.springonlinebanking.models.Users;
import com.uob.springonlinebanking.repositories.AccountRepository;
import com.uob.springonlinebanking.repositories.TransactionRepository;
import com.uob.springonlinebanking.repositories.UserRepository;
import com.uob.springonlinebanking.security.MyUserDetails;

@Controller
public class TransactionController {

	@Autowired
	TransactionRepository transactionRepo;
	@Autowired
	AccountRepository accountRepo;
	@Autowired
	UserRepository userRepo;

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
	public String showAddTransactionForm(HttpServletRequest request, @AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {
		model.addAttribute("transactions", new Transactions());

		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
		if (flashMap != null) {
			model.addAttribute("balAfterWithdrawal", (Double) flashMap.get("balAfterWithdrawal"));
			model.addAttribute("withdrawalAmt", (Double) flashMap.get("withdrawalAmt"));
			model.addAttribute("currBal", (Double) flashMap.get("currBal"));
			model.addAttribute("msg", (String) flashMap.get("msg"));
		}

		Long userId = userDetails.getUserId();
		Users user = userRepo.getUserByUserId(userId);

		List<Long> optionList = new ArrayList<Long>();
		List<Accounts> accountList = user.getAccountList();
		for (Accounts account : accountList) {
			optionList.add(account.getAccountId());
		}

	    model.addAttribute("optionList", optionList);
		Integer count = optionList.size();
		System.out.println(count);
		model.addAttribute("count", count);
		
		return "addTransaction"; // render addTransaction.html
	}

	@PostMapping("/process_transaction")
	public String doTransact(@RequestParam("accId") Long accId, @RequestParam("tType") String tType,
			@RequestParam("transAmount") Double transAmt, Model model, RedirectAttributes redirectAttributes) {

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
		} else if (tType.equals("withdraw")) {
			if (currBal - transAmt < 500) {
				redirectAttributes.addFlashAttribute("balAfterWithdrawal", currBal - transAmt);
				redirectAttributes.addFlashAttribute("withdrawalAmt", transAmt);
				redirectAttributes.addFlashAttribute("currBal", currBal);
				redirectAttributes.addFlashAttribute("msg", "balancelow");
				return "redirect:/addtransaction";
			}
			acct.setBalance(currBal - transAmt);
		}

		accountRepo.save(acct);

		return "welcomeUser";
	}
}
