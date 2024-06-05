package beans;

import java.util.ArrayList;

public class Factory {
	private int id;
	private String name;
	private ArrayList<Chocolate> availableChocolates; //cokolade u ponudi
	private String worktime; //radno vrijeme
	private boolean status; //radi ili ne radi
	public int locationId;
	private String image;
	private double grade;
	
	@Override
	public String toString() {
		return "Factory [id=" + id + ", name=" + name + ", availableChocolates=" + availableChocolates + ", worktime="
				+ worktime + ", status=" + status + ", locationId=" + locationId + ", image=" + image + ", grade="
				+ grade + "]";
	}
	
	public Factory() {
		super();
		this.availableChocolates = new ArrayList<>();
	}
	
	public Factory(int id, String name, ArrayList<Chocolate> availableChocolates, String worktime, boolean status,
			int location, String image, double grade) {
		super();
		this.id = id;
		this.name = name;
		this.availableChocolates = availableChocolates;
		this.worktime = worktime;
		this.status = status;
		this.locationId = location;
		this.image = image;
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
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
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
	
	
	
	
}
