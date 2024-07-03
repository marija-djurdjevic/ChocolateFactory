package beans.roles;

import java.time.LocalDate;

import beans.User;
import beans.enums.Role;

public class Worker extends User {
	public int factoryId;

	public Worker() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Worker(int id, String username, String password, String name, String surname, String gender,
			LocalDate birthDate, Role role, boolean blocked, int factoryId) {
		super(id, username, password, name, surname, gender, birthDate, role, blocked);
		this.factoryId = factoryId;
	}

	public int getFactoryId() {
		return factoryId;
	}

	public void setFactoryId(int factoryId) {
		this.factoryId = factoryId;
	}
	
}
