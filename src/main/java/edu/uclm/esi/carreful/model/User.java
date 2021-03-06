package edu.uclm.esi.carreful.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.commons.codec.digest.DigestUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {
	@Id @Column(length = 80)
	private String email;
	@Lob
	private String pwd;
	@Lob
	private String foto;
	
	
	public User() {
		//constructor vacio
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@JsonIgnore
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = DigestUtils.sha512Hex(pwd);
		
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	
}
