package edu.uclm.esi.carreful.tokens;

import java.time.Duration;
import java.time.LocalTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Token {
	@Id
	@Column(length = 36)
	private String id;
	private String email;
	private LocalTime date;
	private boolean used;
	
	public Token() { }
	
	public Token(String email) {
		this.id = UUID.randomUUID().toString();
		this.email = email;
		this.date =  LocalTime.now();
		this.used = false;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalTime getDate() {
		return date;
	}

	public void setDate(LocalTime date) {
		this.date = date;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}
	
	public boolean checkTime() {
		return Math.abs(Duration.between(LocalTime.now(), this.date).toMinutes()) > 5000.0;
	}
	
	
}
