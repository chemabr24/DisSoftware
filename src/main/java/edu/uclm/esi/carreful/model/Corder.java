package edu.uclm.esi.carreful.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public class Corder {
	
	@Id 
	@Column(length = 36)
	@GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
	protected String id;
	protected double precioTotal;
	protected String state;
	protected String correo;

	public Corder() {
		this.state = "recibido";
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public double getPrecioTotal() {
		return precioTotal;
	}
	
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}

	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	public void nextState() {//Implementados en los hijos
	}
}
