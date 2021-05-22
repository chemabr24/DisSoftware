package edu.uclm.esi.carreful.model;

import java.util.Arrays;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Transient;

@Entity
@PrimaryKeyJoinColumn(referencedColumnName="id")
public class Envio extends Corder {

	private String calle;
	private String localidad;
	private int codigoPostal;
	@Transient
	private final List<String> states = Arrays.asList("recibido", "preparado"," en camino", "entregado") ;

	public Envio() {
		super();
	}

	@Override
	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal + 3.25;
	}

	public String getCalle() {
		return calle;
	}

	public void setCalle(String calle) {
		this.calle = calle;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public int getCodigoPostal() {
		return codigoPostal;
	}

	public void setCodigoPostal(int codigoPostal) {
		this.codigoPostal = codigoPostal;
	}
	
	@Override
	public void nextState() {
		int index = states.indexOf(this.state);
		if(index==(states.size()-1))
			this.state = states.get(0);
		else
			this.state = states.get(index+1);
	}


}
