package com.bank.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bank.model.Transaction;

public interface TransactionRepo extends CrudRepository<Transaction, Long> {
	
	List<Transaction> findAll();
}
