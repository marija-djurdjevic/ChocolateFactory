package beans;

public class FactoryChocolate {

	private int id;
	private int factoryId;
	private int chocolateId;
	
	public FactoryChocolate() {
		super();
		// TODO Auto-generated constructor stub
	}
	public FactoryChocolate(int id, int factoryId, int chocolateId) {
		super();
		this.id = id;
		this.factoryId = factoryId;
		this.chocolateId = chocolateId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFactoryId() {
		return factoryId;
	}
	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}
	public int getChocolateId() {
		return chocolateId;
	}
	public void setChocolateId(int chocolateId) {
		this.chocolateId = chocolateId;
	}
	
	
}
