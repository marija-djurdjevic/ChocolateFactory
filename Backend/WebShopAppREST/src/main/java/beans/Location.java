package beans;

public class Location {
	private int id;
	private String latitude;
	private String longitude;
	private String address;
	
	
	public Location(int id, String latitude, String longitude, String address) {
		super();
		this.id = id;
		this.latitude = latitude; //geo sirina
		this.longitude = longitude; //geo duzina
		this.address = address; //ulica i broj, mesto/grad, poštanski broj
	}
	
	@Override
	public String toString() {
		return "Location [id=" + id + ", latitude=" + latitude + ", longitude=" + longitude + ", address=" + address
				+ "]";
	}

	public Location() {
		super();
	}
	
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	
}
