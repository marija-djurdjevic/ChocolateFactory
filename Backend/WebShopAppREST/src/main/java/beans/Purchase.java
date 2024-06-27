package beans;

import java.time.LocalDateTime;
import java.util.ArrayList;

import beans.enums.PurchaseStatus;

public class Purchase {
	private String id;
	private ArrayList<Chocolate> chocolates;
	private int FactoryId;
	private LocalDateTime DateAndTime;
	private double price;
	private String CustomerName;
	private String CustomerSurname;
	private PurchaseStatus status;
	
	public Purchase() {
		super();
		this.chocolates = new ArrayList<Chocolate>();
	}

	public Purchase(String id, ArrayList<Chocolate> chocolates, int factoryId, LocalDateTime dateAndTime, double price,
			String customerName, String customerSurname, PurchaseStatus status) {
		super();
		this.id = id;
		this.chocolates = chocolates;
		FactoryId = factoryId;
		DateAndTime = dateAndTime;
		this.price = price;
		CustomerName = customerName;
		CustomerSurname = customerSurname;
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
		return FactoryId;
	}

	public void setFactoryId(int factoryId) {
		FactoryId = factoryId;
	}

	public LocalDateTime getDateAndTime() {
		return DateAndTime;
	}

	public void setDateAndTime(LocalDateTime dateAndTime) {
		DateAndTime = dateAndTime;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getCustomerName() {
		return CustomerName;
	}

	public void setCustomerName(String customerName) {
		CustomerName = customerName;
	}

	public String getCustomerSurname() {
		return CustomerSurname;
	}

	public void setCustomerSurname(String customerSurname) {
		CustomerSurname = customerSurname;
	}

	public PurchaseStatus getStatus() {
		return status;
	}

	public void setStatus(PurchaseStatus status) {
		this.status = status;
	}

}
