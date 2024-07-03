package beans;

import dao.ChocolateDAO;

public class CartChocolate {
	private int shoppingCartId;
	private int chocolateId;
	private int amount;
	
	
	public CartChocolate() {
		super();
	}
	
	public CartChocolate(int shoppingCartId, int chocolateId, int amount) {
		super();
		this.shoppingCartId = shoppingCartId;
		this.chocolateId = chocolateId;
		this.amount = amount;
	}

	public int getShoppingCartId() {
		return shoppingCartId;
	}
	public void setShoppingCartId(int shoppingCartId) {
		this.shoppingCartId = shoppingCartId;
	}
	public int getChocolateId() {
		return chocolateId;
	}
	public void setChocolateId(int chocolateId) {
		this.chocolateId = chocolateId;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "CartChocolate [shoppingCartId=" + shoppingCartId + ", chocolateId=" + chocolateId + ", amount=" + amount
				+ "]";
	}
	
	
}
