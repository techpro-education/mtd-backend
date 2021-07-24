package com.bank.service.impl;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.dao.AccountDAO;
import com.bank.model.Account;
import com.bank.service.AccountService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDAO accountDAO;
 
	@Override
	public Account createAccount() {
		Account account = new Account();
		account.setAccountBalance(new BigDecimal(0.0));
		account.setAccountNumber(getAccountNumber());
		accountDAO.save(account);
		return accountDAO.findByAccountNumber(account.getAccountNumber());
	}
	
	private Long getAccountNumber() {
		long smallest = 1000_0000_0000_0000L;
		long biggest = 9999_9999_9999_9999L;
		long random = ThreadLocalRandom.current().nextLong(smallest, biggest);
		return random;
	}
}
