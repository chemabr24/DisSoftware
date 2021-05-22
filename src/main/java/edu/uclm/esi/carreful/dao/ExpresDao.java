package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.Corder;
import edu.uclm.esi.carreful.model.Expres;



@Repository
public interface ExpresDao extends JpaRepository <Expres, String> {

}