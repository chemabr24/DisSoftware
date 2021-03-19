package edu.uclm.esi.carreful.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.Product;



@Repository
public interface ProductDao extends JpaRepository <Product, String> {


}