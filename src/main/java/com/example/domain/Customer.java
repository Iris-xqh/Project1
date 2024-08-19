package com.example.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;


@Entity
public class Customer{
	
	@Id
	public long id;
	
	String name;
	String email;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getEmail() {
		return email;
	}
}
