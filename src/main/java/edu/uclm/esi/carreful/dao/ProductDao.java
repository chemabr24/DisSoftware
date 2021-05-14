package edu.uclm.esi.carreful.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.Product;



@Repository
public interface ProductDao extends JpaRepository <Product, String> {

	Optional<Product> findByNombre(String nombre);

	@Query(value ="SELECT * FROM carreful.product p, carreful.categoria c WHERE p.categoria_id=c.id and c.nombre=:categoria", nativeQuery = true)
	List<Product> findProduct(@Param ("categoria")String categoria);

}