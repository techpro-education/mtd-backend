package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Recipient;
import com.bank.model.User;
import com.bank.request.TransactionRequest;
import com.bank.request.TransferRequest;

public interface AccountService {

	Account createAccount(); 

}
