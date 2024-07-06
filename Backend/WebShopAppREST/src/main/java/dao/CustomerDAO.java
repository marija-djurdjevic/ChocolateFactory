package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.Chocolate;
import beans.CustomerType;
import beans.Factory;
import beans.ShoppingCart;
import beans.User;
import beans.enums.CustomerTypeName;
import beans.enums.Role;
import beans.roles.Customer;

public class CustomerDAO {
	private ArrayList<Customer> customers = new ArrayList<>();
	private DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String contextPath;

	public CustomerDAO(String contextPath) {
		loadCustomers(contextPath);
		this.contextPath = contextPath;
	}
	
	public ArrayList<Customer> findAll() {
		loadCustomers(contextPath);
		return customers;
	}
	
	public Customer update(User userEdit) {
		loadCustomers(contextPath); 
	    for (Customer customer : customers) {
	        if (customer.getId() == userEdit.getId()) {
	            customer.setUsername(userEdit.getUsername());
	            customer.setPassword(userEdit.getPassword());
	            customer.setBirthDate(userEdit.getBirthDate());
	            customer.setGender(userEdit.getGender());
	            customer.setName(userEdit.getName());
	            customer.setSurname(userEdit.getSurname());
	            saveAllCustomers();
	    		loadCustomers(contextPath);
	            return customer;
	            } 
	    }
	    
	    System.out.println("User not found: " + userEdit.getUsername());
	    return null;
	}
	
	private void saveAllCustomers() {
	    try {
	        String filePath = contextPath + "customers.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Customer customer : customers) {
	            String roleString = customer.getRole().toString();
	            System.out.println(roleString);
	        	bufferedWriter.write(customer.getId() + ";" +
	                    customer.getUsername() + ";" +
	                    customer.getPassword() + ";" +
	                    customer.getName() + ";" +
	                    customer.getSurname() + ";" +
	                    customer.getGender() + ";" +
	                    customer.getBirthDate().format(formatter) + ";" +
	                    roleString + ";" +
	                    customer.getPoints() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public Customer updatePoints(int customerId, double price) {
		loadCustomers(contextPath);
		for(Customer customer : customers) {
			if(customer.getId() == customerId) {
				double newPoints = customer.getPoints() + price / 1000 * 133;
				customer.setPoints(newPoints);
				saveAll();
				return customer;
			}
		}
		
		return null;
	}
	
	public Customer updatePointsMinus(int customerId, double price) {
		loadCustomers(contextPath);
		for(Customer customer : customers) {
			if(customer.getId() == customerId) {
				double newPoints = customer.getPoints() - price / 1000 * 133 * 4;
			    System.out.println(newPoints);
				if(newPoints < 0) {
					newPoints = 0;
				}
				
			    System.out.println(newPoints);
				customer.setPoints(newPoints);
				saveAll();
				return customer;
			}
		}
		
		return null;
	}
	
	public void saveAll() {
		try {
	        String filePath = contextPath + "customers.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Customer customer : customers) {
	            String roleString = customer.getRole().toString();
	        	bufferedWriter.write(customer.getId() + ";" +
	            		customer.getUsername() + ";" +
	            		customer.getPassword() + ";" +
	            		customer.getName() + ";" +
	            		customer.getSurname() + ";" +
	            		customer.getGender() + ";" +
	            		customer.getBirthDate().format(formatter) + ";" +
	            		roleString + ";" +
	            		customer.isBlocked() + ";" +
	                    customer.getPoints() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public Customer save(User user, String contextPath) {
		loadCustomers(contextPath);
		Customer customer = new Customer(user.getId(), user.getUsername(), user.getPassword(), user.getName(), user.getSurname(), user.getGender(), user.getBirthDate(), user.getRole(), user.isBlocked());
		customer.setPoints(0.0);
		customer.setPurchases(null);
		customer.setShoppingCart(null);
		customer.setType(null);
		
        try {
            String filePath = contextPath + "customers.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            String roleString = customer.getRole().toString();
            System.out.println(roleString);
            bufferedWriter.write(customer.getId() + ";" +
            		customer.getUsername() + ";" +
            		customer.getPassword() + ";" +
            		customer.getName() + ";" +
            		customer.getSurname() + ";" +
            		customer.getGender() + ";" +
            		customer.getBirthDate().format(formatter) + ";" +
            		roleString + ";" +
            		customer.isBlocked() + ";" +
                    customer.getPoints() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return customer; // Return the saved Chocolate object
    }
	
	private void loadCustomers(String contextPath) {
		this.customers = new ArrayList<Customer>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/customers.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				String username = st.nextToken().trim();
				String password = st.nextToken().trim();
				String name = st.nextToken().trim();
				String surname = st.nextToken().trim();
				String gender = st.nextToken().trim();
				LocalDate birthDate = LocalDate.parse(st.nextToken().trim(), formatter); 
				Role role = Role.valueOf(st.nextToken().trim());
				boolean blocked = Boolean.parseBoolean(st.nextToken().trim());
	            System.out.println(role);
				double points = Double.parseDouble(st.nextToken().trim());
				ShoppingCart shoppingCart = new ShoppingCart();
				CustomerType customerType = new CustomerType();
;				customers.add(new Customer(id, username, password, name, surname, gender, birthDate, role, blocked, customerType, shoppingCart, points));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	public Customer findById(int id) {
	    for (Customer customer : customers) {
	        if (customer.getId() == id) {
	            return customer;
	        }
	    }
	    return null; 
	}
	
}
