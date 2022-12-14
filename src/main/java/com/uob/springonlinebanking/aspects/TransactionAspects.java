package com.uob.springonlinebanking.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.uob.springonlinebanking.SpringOnlineBankingApplication;
import com.uob.springonlinebanking.models.Accounts;
import com.uob.springonlinebanking.repositories.AccountRepository;
import com.uob.springonlinebanking.repositories.TransactionRepository;

@Aspect
@Component
public class TransactionAspects {

	@Autowired
	AccountRepository accountRepo;
	@Autowired
	TransactionRepository transactionRepo;

	Logger logger = LoggerFactory.getLogger(SpringOnlineBankingApplication.class);

	@Around("execution (public * doTransact(..)) and args(accId,tType,txnAmt,model,redirectAttributes)")
	public String chkAmt(ProceedingJoinPoint pjp, Long accId, String tType, Double txnAmt, Model model,
			RedirectAttributes redirectAttributes) throws Throwable {
		Accounts acct = accountRepo.findByAccountId(accId);
		Double currBal = acct.getBalance();
		
		if (tType.equals("withdraw")) {
			Double balAfterWithdrawal = currBal - txnAmt;
			
			if (balAfterWithdrawal < 500) {
				logger.info("Withdrawal not allowed, balance after withdrawal is below $500");
			} else {
				logger.info("Withdrawal successfull");
			}
		} else {
			logger.info("Deposit successfull");
		}
		return (String) pjp.proceed();
	}
}
