package beans;

import java.time.LocalDateTime;
import java.util.ArrayList;

import beans.enums.PurchaseStatus;

public class Purchase {
	private String id;
	public ArrayList<Chocolate> chocolates;
	private int factoryId;
	private LocalDateTime dateAndTime;
	private double price;
	private int customerId; //treba dobaviti ime i prezime kupca
	private PurchaseStatus status;
	
	public Purchase() {
		super();
		this.chocolates = new ArrayList<Chocolate>();
	}

	public Purchase(String id, ArrayList<Chocolate> chocolates, int factoryId, LocalDateTime dateAndTime, double price,
			int customerId, PurchaseStatus status) {
		super();
		this.id = id;
		this.chocolates = chocolates;
		this.factoryId = factoryId;
		this.dateAndTime = dateAndTime;
		this.price = price;
		this.customerId = customerId;
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Chocolate> getChocolates() {
		return chocolates;
	}

	public void setChocolates(ArrayList<Chocolate> chocolates) {
		this.chocolates = chocolates;
	}

	public int getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}

	public LocalDateTime getDateAndTime() {
		return dateAndTime;
	}

	public void setDateAndTime(LocalDateTime dateAndTime) {
		this.dateAndTime = dateAndTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public PurchaseStatus getStatus() {
		return status;
	}

	public void setStatus(PurchaseStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Purchase [id=" + id + ", chocolates=" + chocolates + ", FactoryId=" + factoryId + ", DateAndTime="
				+ dateAndTime + ", price=" + price + ", customerId=" + customerId + ", status=" + status + "]";
	}
	
	public boolean containsChocolateWithId(int chocolateId) {
        for (Chocolate chocolate : chocolates) {
            if (chocolate.getId() == chocolateId) {
                return true;
            }
        }
        return false;
    }
	
}
