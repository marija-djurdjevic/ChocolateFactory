package beans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
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
	private String imageString;
	private boolean available;
	private int amountOfChocolate;
	
	public Chocolate() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Chocolate(int id, String name, double price, String chocolateSort, int factoryId, String chocolateType,
			int gramsOfChocolate, String chocolateDescription, String imagePath, String imageString, boolean available,
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
		this.imageString = imageString;
		this.available = available;
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
		return available;
	}

	public void setAvailable(boolean isAvailable) {
		this.available = isAvailable;
	}

	public int getAmountOfChocolate() {
		return amountOfChocolate;
	}

	public void setAmountOfChocolate(int amountOfChocolate) {
		this.amountOfChocolate = amountOfChocolate;
	}

	public String getImageString() {
		return imageString;
	}

	public void setImageString(String imageString) {
		this.imageString = imageString;
	}

	@Override
	public String toString() {
		return "Chocolate [id=" + id + ", name=" + name + ", price=" + price + ", chocolateSort=" + chocolateSort + ", factoryId="
				+ factoryId + ", chocolateType=" + chocolateType + ", gramsOfChocolate=" + gramsOfChocolate
				+ ", chocolateDescription=" + chocolateDescription + ", imagePath=" + imagePath + ", isAvailable="
				+ available + ", amountOfChocolate=" + amountOfChocolate + "]";
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
				&& Objects.equals(imagePath, other.imagePath) && available == other.available
				&& Objects.equals(name, other.name);
	}

	public double getPrice() {
		return price;
	}

	public void loadImageString() {
		File file = new File(this.imagePath);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
		
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead;
			while((bytesRead = fis.read(buffer)) != -1) {
				bos.write(buffer, 0, bytesRead);
			}
			
			byte[] imageBytes = bos.toByteArray();
			String base64String = Base64.getEncoder().encodeToString(imageBytes);
			fis.close();
			bos.close();
			this.imageString = base64String;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
