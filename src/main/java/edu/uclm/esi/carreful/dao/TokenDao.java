package edu.uclm.esi.carreful.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.User;
import edu.uclm.esi.carreful.tokens.Token;

@Repository
public interface TokenDao extends JpaRepository <Token, String> {

	List<Token> findByEmail(String email);
	
}
