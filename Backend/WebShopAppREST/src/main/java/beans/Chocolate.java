package beans;

import java.util.Objects; 

public class Chocolate {
	private int id;
	private String name;
	private double price;
	private String chocolateSort; //obična, za kuvanje
	private int factoryId;
	private String chocolateType; //crna, bijela
	private int gramsOfChocolate;
	private String chocolateDescription;
	private String imagePath;
	private boolean isAvailable;
	private int amountOfChocolate;
	
	public Chocolate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Chocolate(int id, String name, double price, String chocolateSort, int factoryId, String chocolateType,
			int gramsOfChocolate, String chocolateDescription, String imagePath, boolean isAvailable,
			int amountOfChocolate) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
		this.chocolateSort = chocolateSort;
		this.factoryId = factoryId;
		this.chocolateType = chocolateType;
		this.gramsOfChocolate = gramsOfChocolate;
		this.chocolateDescription = chocolateDescription;
		this.imagePath = imagePath;
		this.isAvailable = isAvailable;
		this.amountOfChocolate = amountOfChocolate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public String getChocolateSort() {
		return chocolateSort;
	}

	public void setChocolateSort(String chocolateSort) {
		this.chocolateSort = chocolateSort;
	}

	public int getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}

	public String getChocolateType() {
		return chocolateType;
	}

	public void setChocolateType(String chocolateType) {
		this.chocolateType = chocolateType;
	}

	public int getGramsOfChocolate() {
		return gramsOfChocolate;
	}

	public void setGramsOfChocolate(int gramsOfChocolate) {
		this.gramsOfChocolate = gramsOfChocolate;
	}

	public String getChocolateDescription() {
		return chocolateDescription;
	}

	public void setChocolateDescription(String chocolateDescription) {
		this.chocolateDescription = chocolateDescription;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public int getAmountOfChocolate() {
		return amountOfChocolate;
	}

	public void setAmountOfChocolate(int amountOfChocolate) {
		this.amountOfChocolate = amountOfChocolate;
	}

	@Override
	public String toString() {
		return "Chocolate [id=" + id + ", name=" + name + ", price=" + price + ", chocolateSort=" + chocolateSort + ", factoryId="
				+ factoryId + ", chocolateType=" + chocolateType + ", gramsOfChocolate=" + gramsOfChocolate
				+ ", chocolateDescription=" + chocolateDescription + ", imagePath=" + imagePath + ", isAvailable="
				+ isAvailable + ", amountOfChocolate=" + amountOfChocolate + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chocolate other = (Chocolate) obj;
		return amountOfChocolate == other.amountOfChocolate
				&& Objects.equals(chocolateDescription, other.chocolateDescription)
				&& Objects.equals(price, other.price)
				&& Objects.equals(chocolateSort, other.chocolateSort)
				&& Objects.equals(chocolateType, other.chocolateType) && factoryId == other.factoryId
				&& gramsOfChocolate == other.gramsOfChocolate && id == other.id
				&& Objects.equals(imagePath, other.imagePath) && isAvailable == other.isAvailable
				&& Objects.equals(name, other.name);
	}
	
	
	
}
