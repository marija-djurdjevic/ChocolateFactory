package beans;

import beans.enums.CustomerTypeName;

public class CustomerType {
	private CustomerTypeName typeName;
	private double discount;
	private int points;
	
	public CustomerType() {
		super();
	}

	public CustomerType(CustomerTypeName typeName, double discount, int points) {
		super();
		this.typeName = typeName;
		this.discount = discount;
		this.points = points;
	}

	public CustomerTypeName getTypeName() {
		return typeName;
	}

	public void setTypeName(CustomerTypeName typeName) {
		this.typeName = typeName;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}
	
	
	
}
