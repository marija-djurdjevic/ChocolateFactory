package beans;

import java.util.ArrayList;

public class ShoppingCart {
	private int id;
	private int customerId;
	private ArrayList<Chocolate> chocolates;
	private double price;
	
	public ShoppingCart() {
		super();
		this.chocolates = new ArrayList<Chocolate>();
	}

	public ShoppingCart(int id, int customerId, ArrayList<Chocolate> chocolates, double price) {
		super();
		this.id = id;
		this.customerId = customerId;
		this.chocolates = chocolates;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public ArrayList<Chocolate> getChocolates() {
		return chocolates;
	}

	public void setChocolates(ArrayList<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
}
