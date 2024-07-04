package beans;

import dao.ChocolateDAO;

public class CartChocolate {
	private int shoppingCartId;
	private int chocolateId;
	private int amount;
	private String purchaseId;
	
	
	public CartChocolate() {
		super();
	}
	
	public CartChocolate(int shoppingCartId, int chocolateId, int amount, String purchaseId) {
		super();
		this.shoppingCartId = shoppingCartId;
		this.chocolateId = chocolateId;
		this.amount = amount;
		this.purchaseId = purchaseId;
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
	
	
	public String getPurchaseId() {
		return purchaseId;
	}

	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}

	@Override
	public String toString() {
		return "CartChocolate [shoppingCartId=" + shoppingCartId + ", chocolateId=" + chocolateId + ", amount=" + amount
				+ ", purchaseId=" + purchaseId + "]";
	}
	
}
