package com.bank.service.impl;

import static com.bank.util.DateUtil.SIMPLE_DATE_FORMAT;
import static com.bank.util.DateUtil.SIMPLE_DATE_TIME_FORMAT;
import static com.bank.util.DateUtil.getDateAsString;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bank.dao.RecipientDAO;
import com.bank.dao.TransactionDAO;
import com.bank.dao.UserDAO;
import com.bank.model.Recipient;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepo;
import com.bank.repository.TransactionRepo;
import com.bank.repository.UserRepo;
import com.bank.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {

	@Autowired
	UserRepo userRepo;

	@Autowired
	TransactionRepo transactionRepo;

	@Autowired
	AccountRepo accountRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User Not Found with  username  " + username));
	}

	@Override
	public UserDAO getUserDAO(User user) {
		UserDAO userDAO = new UserDAO();
		userDAO.setUsername(user.getUsername());
		userDAO.setFirstName(user.getFirstName());
		userDAO.setLastName(user.getLastName());
		userDAO.setEmail(user.getEmail());
		userDAO.setPhone(user.getPhone());

		// Check User has admin role
		boolean isAdminExist = user.getUserRoles().stream()
				.anyMatch(userRole -> userRole.getRole().getName().equalsIgnoreCase("admin"));

		userDAO.setIsAdmin(isAdminExist);

		if (isAdminExist) {
			// Get all transactions from DB
			List<Transaction> transactions = transactionRepo.findAll();

			// Convert Transaction Object to TransactionDAO as store in List
			List<TransactionDAO> transactionDAOs = transactions.stream().map(this::getTransactionDAO)
					.collect(Collectors.toList());
			userDAO.setTransactions(transactionDAOs);

			// Set the total user count
			userDAO.setTotalUsers(userRepo.count());

			// Set Bank Total Balance
			Double totalBalance = accountRepo.findAll().stream()
					.mapToDouble(account -> account.getAccountBalance().doubleValue()).sum();
			userDAO.setTotalBalance(totalBalance);
		} else if (user.getAccount() != null) {
			userDAO.setAccountNumber(user.getAccount().getAccountNumber());
			userDAO.setAccountBalance(user.getAccount().getAccountBalance());

			// Convert Transaction Object to TransactionDAO as store in List
			List<TransactionDAO> transactionDAOs = user.getAccount().getTransactions().stream()
					.map(this::getTransactionDAO).collect(Collectors.toList());
			userDAO.setTransactions(transactionDAOs);

			// Add Recipient Information
			List<RecipientDAO> recipients = user.getRecipients().stream().map(this::getRecipientDAO)
					.collect(Collectors.toList());
			userDAO.setRecipients(recipients);
		}
		return userDAO;
	}

	@Override
	public UserDAO getUserDAOByName(String username) {

		// Initialize the User DAO Object
		UserDAO userDAO = null;

		// Get the User Object from Database
		Optional<User> userOpt = userRepo.findByUsername(username);

		// Check the User Object is present or not
		if (userOpt.isPresent()) {

			// Convert User Object to User DAO Object
			userDAO = getUserDAO(userOpt.get());
		}

		// return the user dao object
		return userDAO;
	}

	@Override
	public List<UserDAO> getAllUsers() {
		return userRepo.findAll().stream().map(this::transformUser).collect(Collectors.toList());
	}

	@Override
	public void deleteUser(Long id) {
		userRepo.deleteById(id);
	}

	private UserDAO transformUser(User user) {
		UserDAO userDAO = new UserDAO();
		userDAO.setUserId(user.getUserId());
		userDAO.setFirstName(user.getFirstName());
		userDAO.setLastName(user.getLastName());
		userDAO.setEmail(user.getEmail());
		return userDAO;
	}

	private TransactionDAO getTransactionDAO(Transaction transaction) {
		TransactionDAO transactionDAO = new TransactionDAO();
		transactionDAO.setId(transaction.getId());
		transactionDAO.setDate(getDateAsString(transaction.getDate(), SIMPLE_DATE_FORMAT));
		transactionDAO.setTime(getDateAsString(transaction.getDate(), SIMPLE_DATE_TIME_FORMAT));
		transactionDAO.setDescription(transaction.getDescription());
		transactionDAO.setType(transaction.getType());
		transactionDAO.setAmount(transaction.getAmount());
		transactionDAO.setAvailableBalance(transaction.getAvailableBalance());
		transactionDAO.setTransfer(transaction.isTransfer());
		return transactionDAO;
	}

	private RecipientDAO getRecipientDAO(Recipient recipient) {
		RecipientDAO recipientDAO = new RecipientDAO();
		recipientDAO.setId(recipient.getId());
		recipientDAO.setName(recipient.getName());
		recipientDAO.setEmail(recipient.getEmail());
		recipientDAO.setPhone(recipient.getPhone());
		recipientDAO.setBankName(recipient.getBankName());
		recipientDAO.setBankNumber(recipient.getBankNumber());
		return recipientDAO;
	}

}
