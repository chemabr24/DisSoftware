package edu.uclm.esi.carreful.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product {
	@Id
	private String nombre;
	private String precio;
	private String codigo;
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getPrecio() {
		return precio;
	}
	
	public void setPrecio(String precio) {
		this.precio = precio;
	}
	
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	public String getCodigo() {
		return codigo;
	}
}
