package com.uob.springonlinebanking.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	// ============================================= Add Transactions
	@GetMapping("/addtransaction") // used in welcomeUser.html
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

		Map<Long, String> optionList = new HashMap<Long, String>();
		List<Accounts> savingAccountList = accountRepo.getAllAccountsByUserIdAndAccountType(userId, "Savings");
		for (Accounts account : savingAccountList) {
			if (!account.isDormant()) { // only allow transaction on active account
				String stringValue = account.getAccountId() + " - " + account.getAccountType() + " (Balance: $" + account.getBalance() + ")";
				optionList.put(account.getAccountId(), stringValue);
			}
		}

		model.addAttribute("optionList", optionList);
		Integer count = optionList.size();
		model.addAttribute("count", count);

		return "addTransaction"; // render addTransaction.html
	}

	@PostMapping("/process_transaction") // used in addTransaction.html
	public String doTransact(@RequestParam("accId") Long accId, @RequestParam("tType") String tType,
			@RequestParam("txnAmt") Double txnAmt, Model model, RedirectAttributes redirectAttributes) {

		Accounts acct = accountRepo.findByAccountId(accId);

		Transactions transaction = new Transactions();
		transaction.setTransactionAmount(txnAmt);
		transaction.setTxnType(tType);
		transaction.setAccount(acct);
		transaction.setDateTime(LocalDateTime.now());
		transaction.setDormant(false);

		Double currBal = acct.getBalance();

		redirectAttributes.addFlashAttribute("txnAmt", txnAmt);

		if (tType.equals("deposit")) {
			if (acct.getBalance() == 0.0 && txnAmt < 500.0) {
				redirectAttributes.addFlashAttribute("msg", "firsttxn");
				transaction.setStatus("failure");
				transaction.setMsg("First deposit below $500");
				transactionRepo.save(transaction);
				return "redirect:/addtransaction";
			}
			redirectAttributes.addFlashAttribute("balAfterDeposit", currBal + txnAmt);
			redirectAttributes.addFlashAttribute("msg", "deposit");
			acct.setBalance(currBal + txnAmt);
		} else if (tType.equals("withdraw")) {
			Double balAfterWithdrawal = currBal - txnAmt;
			redirectAttributes.addFlashAttribute("balAfterWithdrawal", balAfterWithdrawal);
			if (balAfterWithdrawal < 500) {
				redirectAttributes.addFlashAttribute("currBal", currBal);
				redirectAttributes.addFlashAttribute("msg", "balancelow");
				transaction.setStatus("failure");
				transaction.setMsg("Balance below $500 after withdrawal");
				transactionRepo.save(transaction);
				return "redirect:/addtransaction";
			}
			redirectAttributes.addFlashAttribute("msg", "withdraw");
			acct.setBalance(balAfterWithdrawal);
		}

		redirectAttributes.addFlashAttribute("accountNo", acct.getAccountId());
		transaction.setStatus("success");
		transactionRepo.save(transaction);
		accountRepo.save(acct);

		return "redirect:/welcomeuser";
	}
	
	// ============================================= Renew Deposit
		@GetMapping("/renewdeposit/{totalBalance}") // used in welcomeUser.html
		public String showRenewDeposit(@PathVariable("totalBalance") Double balance, HttpServletRequest request, @AuthenticationPrincipal MyUserDetails userDetails,
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
				if (!account.isDormant()) { // only allow transaction on active account 
					optionList.add(account.getAccountId());
				}
			}

			model.addAttribute("optionList", optionList);
			Integer count = optionList.size();
			model.addAttribute("count", count);

			return "renewDeposit"; // render renewDeposit.html
		}

		@PostMapping("/process_renewdeposit/") // used in addTransaction.html
		public String doRenewDeposit(@RequestParam("accId") Long accId, @RequestParam("tType") String tType,
				@RequestParam("txnAmt") Double txnAmt, Model model, RedirectAttributes redirectAttributes) {

			Accounts acct = accountRepo.findByAccountId(accId);

			Transactions transaction = new Transactions();
			transaction.setTransactionAmount(txnAmt);
			transaction.setTxnType(tType);
			transaction.setAccount(acct);
			transaction.setDateTime(LocalDateTime.now());
			transaction.setDormant(false);

			Double currBal = acct.getBalance();

			redirectAttributes.addFlashAttribute("txnAmt", txnAmt);

			if (tType.equals("deposit")) {
				redirectAttributes.addFlashAttribute("balAfterDeposit", currBal + txnAmt);
				redirectAttributes.addFlashAttribute("msg", "deposit");
				acct.setBalance(currBal + txnAmt);
			} else if (tType.equals("withdraw")) {
				Double balAfterWithdrawal = currBal - txnAmt;
				redirectAttributes.addFlashAttribute("balAfterWithdrawal", balAfterWithdrawal);
				if (balAfterWithdrawal < 500) {
					redirectAttributes.addFlashAttribute("currBal", currBal);
					redirectAttributes.addFlashAttribute("msg", "balancelow");
					transaction.setStatus("failure");
					transaction.setMsg("Balance below $500 after withdrawal");
					transactionRepo.save(transaction);
					return "redirect:/addtransaction";
				}
				redirectAttributes.addFlashAttribute("msg", "withdraw");
				acct.setBalance(balAfterWithdrawal);
			}

			redirectAttributes.addFlashAttribute("accountNo", acct.getAccountId());
			transaction.setStatus("success");
			transactionRepo.save(transaction);
			accountRepo.save(acct);

			return "redirect:/welcomeuser";
		}
}
