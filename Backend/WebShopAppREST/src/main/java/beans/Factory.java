package beans;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;

public class Factory {
	private int id;
	private String name;
	private ArrayList<Chocolate> availableChocolates; //cokolade u ponudi
	private String worktime; //radno vrijeme
	private boolean status; //radi ili ne radi
	public int locationId;
	private String imagePath;
	private String imageString;
	private double grade;
	
	public Factory() {
		super();
		this.availableChocolates = new ArrayList<>();
	}
	
	public Factory(int id, String name, ArrayList<Chocolate> availableChocolates, String worktime, boolean status,
			int location, String imagePath, String imageString, double grade) {
		super();
		this.id = id;
		this.name = name;
		this.availableChocolates = availableChocolates;
		this.worktime = worktime;
		this.status = status;
		this.locationId = location;
		this.imagePath = imagePath;
		this.imageString = imageString;
		this.grade = grade;
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
	public ArrayList<Chocolate> getAvailableChocolates() {
		for(Chocolate c: availableChocolates) {
			c.loadImageString();
		}
		return availableChocolates;
	}
	public void setAvailableChocolates(ArrayList<Chocolate> availableChocolates) {
		this.availableChocolates = availableChocolates;
	}
	public String getWorktime() {
		return worktime;
	}
	public void setWorktime(String worktime) {
		this.worktime = worktime;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public int getLocation() {
		return locationId;
	}
	public void setLocation(int location) {
		this.locationId = location;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	
	
	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getImageString() {
		return imageString;
	}

	public void setImageString(String imageString) {
		this.imageString = imageString;
	}

	public double getGrade() {
		return grade;
	}
	public void setGrade(double grade) {
		this.grade = grade;
	}

	public void addChocolateToFactory(Chocolate chocolate) {
		this.availableChocolates.add(chocolate);
		
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

	@Override
	public String toString() {
		return "Factory [id=" + id + ", name=" + name + ", availableChocolates=" + availableChocolates + ", worktime="
				+ worktime + ", status=" + status + ", locationId=" + locationId + ", imagePath=" + imagePath
				+ ", imageString=" + imageString + ", grade=" + grade + "]";
	}
	
	
	
}
