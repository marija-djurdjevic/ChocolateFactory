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
import beans.Factory;
import beans.User;
import beans.enums.Role;


public class UserDAO {
	private ArrayList<User> users = new ArrayList<>();
	private DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String contextPath;

	public UserDAO(String contextPath) {
		loadUsers(contextPath);
		this.contextPath = contextPath;
	}
	
	public ArrayList<User> findAll() {
		loadUsers(contextPath);
		return users;
	}
	
	public User save(User user) {
		loadUsers(contextPath);
        int maxId = -1;
        for (User u : users) {
            if (u.getId() > maxId) {
                maxId = u.getId();
            }
        }
        maxId++;
        user.setId(maxId);
        
        try {
            String filePath = contextPath + "users.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            user.setRole(Role.Customer);
            String roleString = user.getRole().toString();
            bufferedWriter.write(user.getId() + ";" +
                    user.getUsername() + ";" +
                    user.getPassword() + ";" +
                    user.getName() + ";" +
                    user.getSurname() + ";" +
                    user.getGender() + ";" +
                    user.getBirthDate().format(formatter) + ";" +
                    roleString + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        CustomerDAO dao = new CustomerDAO(contextPath);
        dao.save(user, contextPath);
        
        return user; // Return the saved Chocolate object
    }
	
	private void loadUsers(String contextPath) {
		this.users = new ArrayList<User>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/users.txt");
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
				Role role = Role.valueOf(st.nextToken().trim().toUpperCase());				
				User user = new User(id, username, password, name, surname, gender, birthDate, role);
				users.add(user);

				
				System.out.println("Loaded user: " + user);

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
	
	
	 public User authenticateUser(String username, String password) {
	        loadUsers(contextPath); // Ensure users are loaded
	        for (User user : users) {
	            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
	                return user;
	            }
	        }
	        return null;
	}
	
}
