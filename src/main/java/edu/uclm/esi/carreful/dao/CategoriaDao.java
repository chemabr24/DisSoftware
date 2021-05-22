package edu.uclm.esi.carreful.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.carreful.model.Categoria;



@Repository
public interface CategoriaDao extends JpaRepository <Categoria, String> {

	Optional<Categoria> findByNombre(String nombre);

	@Query(value ="SELECT nombre FROM carreful.categoria", nativeQuery = true)
	List<String> findAllNames();
}