package edu.uclm.esi.carreful.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class Expres extends Envio {

	public Expres() {
		super();
	}

	@Override
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal + 5.5;
	}

}
