package edu.uclm.esi.carreful.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.Envio;



@Repository
public interface EnvioDao extends JpaRepository <Envio, String> {

}