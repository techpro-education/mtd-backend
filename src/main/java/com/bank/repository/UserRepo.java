package com.bank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bank.model.User;

/**
 * The Class UserRepo is used to connect user table.
 */
public interface UserRepo extends CrudRepository<User, Long>{
	
	Optional<User> findByUsername(String userName);
	
	boolean existsByUsername(String userName);
	
	boolean existsByEmail(String email);
	
	List<User> findAll(); 

	long count();
}
