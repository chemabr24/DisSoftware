package edu.uclm.esi.carreful.model;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class Recogida extends Corder{

	private String tienda;
	
	public Recogida() {
		super();
	}
	
	@Override
	public void nextState() {
		if(this.state.equals("recibido")) {
			this.state = "preparado";
		}else {
			this.state = "recibido";
		}
	}

	public String getTienda() {
		return tienda;
	}

	public void setTienda(String tienda) {
		this.tienda = tienda;
	}
	
}
