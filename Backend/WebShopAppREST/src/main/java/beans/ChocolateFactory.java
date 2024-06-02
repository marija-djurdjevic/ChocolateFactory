package beans;

import java.util.ArrayList;

public class ChocolateFactory {
	private int id;
	private String name;
	private ArrayList<Chocolate> availableChocolates; //cokolade u ponudi
	private String worktime; //radno vrijeme
	private boolean status; //radi ili ne radi
	private Location location;
	private String image;
	private double grade;
	
	
	public ChocolateFactory() {
		super();
		this.availableChocolates = new ArrayList<>();
	}
	
	
	public ChocolateFactory(int id, String name, List<Chocolate> availableChocolates, String worktime, boolean status,
			Location location, String image, double grade) {
		super();
		this.id = id;
		this.name = name;
		this.availableChocolates = availableChocolates;
		this.worktime = worktime;
		this.status = status;
		this.location = location;
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
	public List<Chocolate> getAvailableChocolates() {
		return availableChocolates;
	}
	public void setAvailableChocolates(List<Chocolate> availableChocolates) {
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
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
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
	
	
	
	
}
