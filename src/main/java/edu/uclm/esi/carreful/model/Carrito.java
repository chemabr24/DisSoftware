package edu.uclm.esi.carreful.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

public class Carrito implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private transient HashMap<String, OrderedProduct> products;
	
	public Carrito() {
		this.products = new HashMap<>();
	}

	public void add(Product product, double amount) {
		OrderedProduct orderedProduct = this.products.get(product.getNombre());
		if (orderedProduct==null) {
			orderedProduct = new OrderedProduct(product, amount);
			this.products.put(product.getNombre(), orderedProduct);
		} else {
			orderedProduct.addAmount(amount);
		}
	}
	
	public void sub(Product product, int amount) {
		OrderedProduct orderedProduct = this.products.get(product.getNombre());
		if (orderedProduct!=null) {
			double oPamount = orderedProduct.getAmount();
			if(oPamount-amount <= 0) {
				this.products.remove(product.getNombre());
			}else {
				orderedProduct.subAmount(amount);
				this.products.replace(product.getNombre(),orderedProduct);
			}
		}
	}

	public Collection<OrderedProduct> getOproducts() {
		return this.products.values();
	}

	public double getImporte() {
		Collection<OrderedProduct> orderedProducts = this.products.values();
		double importe = 0;
		for(OrderedProduct orderedProduct : orderedProducts) {
			importe+=orderedProduct.getAmount()*orderedProduct.getPrice();
		}
		return importe;
	}

	
}
