package com.bank.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bank.model.Account;
import com.bank.model.Recipient;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepo;
import com.bank.repository.RecipientRepo;
import com.bank.request.TransactionRequest;
import com.bank.request.TransferRequest;
import com.bank.service.AccountService;
import com.bank.service.TransactionService;
import com.bank.util.TransactionType;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	AccountRepo accountRepo;

	@Autowired
	RecipientRepo recipientRepo;

	@Autowired
	TransactionService transactionService;

	@Override
	public Account createAccount() {
		Account account = new Account();
		account.setAccountBalance(BigDecimal.valueOf(0.0));
		account.setAccountNumber(getAccountNumber());
		accountRepo.save(account);
		return accountRepo.findByAccountNumber(account.getAccountNumber());
	}

	@Override
	public void deposit(TransactionRequest request, User user) {

		// Update the Account Balance
		Account account = user.getAccount();
		double amount = request.getAmount();
		account.setAccountBalance(account.getAccountBalance().add(BigDecimal.valueOf(amount)));
		accountRepo.save(account);

		// Update the Transaction
		Transaction transaction = new Transaction(new Date(), request.getComment(), TransactionType.DEPOSIT.toString(),
				amount, account.getAccountBalance(), false, account);
		transactionService.saveTransaction(transaction);

	}

	@Override
	public void withdraw(TransactionRequest request, User user) {
		// Update the Account Balance
		Account account = user.getAccount();
		double amount = request.getAmount();
		account.setAccountBalance(account.getAccountBalance().subtract(BigDecimal.valueOf(amount)));
		accountRepo.save(account);

		// Update the Transaction
		Transaction transaction = new Transaction(new Date(), request.getComment(), TransactionType.WITHDRAW.toString(),
				amount, account.getAccountBalance(), false, account);
		transactionService.saveTransaction(transaction);

	}

	@Override
	public void saveRecipient(Recipient recipient) {
		recipientRepo.save(recipient);
	}

	@Override
	public void transfer(TransferRequest request, User user) {
		// Update the Account Balance
		Account account = user.getAccount();
		double amount = request.getAmount();
		account.setAccountBalance(account.getAccountBalance().subtract(BigDecimal.valueOf(amount)));
		accountRepo.save(account);

		// Update the Transaction
		Transaction transaction = new Transaction(new Date(), "Transferred to " + request.getRecipientName(),
				TransactionType.TRANSFER.toString(), amount, account.getAccountBalance(), true, account);
		transactionService.saveTransaction(transaction);

	}

	private Long getAccountNumber() {
		long smallest = 1000_0000_0000_0000L;
		long biggest = 9999_9999_9999_9999L;
		return ThreadLocalRandom.current().nextLong(smallest, biggest);
	}
}
