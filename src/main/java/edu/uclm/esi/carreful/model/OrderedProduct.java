package edu.uclm.esi.carreful.model;

public class OrderedProduct {
	private Product product;
	private double amount;
	
	
	public OrderedProduct(Product product, double amount) {
		this.product = product;
		this.amount = amount;
	}


	public void addAmount(int amount) {
		this.amount+=amount;
		
	}

	public double getAmount() {
		return amount;
	}
	
	public void setAmount(double amount) {
		this.amount = amount;
	}
	
	public String getName() {
		return this.product.getNombre();
		
	}
	
	
	
}
