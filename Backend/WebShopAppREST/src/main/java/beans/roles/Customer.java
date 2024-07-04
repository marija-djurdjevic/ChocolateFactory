package beans.roles;

import java.time.LocalDate;
import java.util.ArrayList;

import beans.CustomerType;
import beans.Purchase;
import beans.ShoppingCart;
import beans.User;
import beans.enums.Role;

public class Customer  extends User {
	private ArrayList<Purchase> purchases;
	private CustomerType type;
	private ShoppingCart shoppingCart;
	private double points;
	
	public Customer() {
		super();
		purchases = new ArrayList<Purchase>();
	}
	
	public Customer(int id, String username, String password, String name, String surname, String gender,
			LocalDate birthDate, Role role, boolean blocked,  CustomerType type, ShoppingCart shoppingCart, double points) {
		super(id, username, password, name, surname, gender, birthDate, role, blocked);
		this.type = type;
		this.shoppingCart = shoppingCart;
		this.points = points;
		
	}
	
	public Customer(int id, String username, String password, String name, String surname, String gender,
			LocalDate birthDate, Role role, boolean blocked) {
		super(id, username, password, name, surname, gender, birthDate, role, blocked);		
	}
	public ArrayList<Purchase> getPurchases() {
		return purchases;
	}
	public void setPurchases(ArrayList<Purchase> purchases) {
		this.purchases = purchases;
	}
	public CustomerType getType() {
		return type;
	}
	public void setType(CustomerType type) {
		this.type = type;
	}
	public ShoppingCart getShoppingCart() {
		return shoppingCart;
	}
	public void setShoppingCart(ShoppingCart shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
	public double getPoints() {
		return points;
	}
	public void setPoints(double points) {
		this.points = points;
	}

	
	
	
	
}
