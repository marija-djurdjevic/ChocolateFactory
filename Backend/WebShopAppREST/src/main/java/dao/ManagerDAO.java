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

import beans.roles.Customer;
import beans.roles.Manager;
import beans.Chocolate;
import beans.User;
import beans.enums.Role;

public class ManagerDAO {
    private ArrayList<Manager> managers = new ArrayList<>();
    private ArrayList<Manager> availableManagers = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String contextPath;

    public ManagerDAO(String contextPath) {
        loadManagers(contextPath);
        this.contextPath = contextPath;
    }

    public ArrayList<Manager> findAll() {
        loadManagers(contextPath);
        return managers;
    }
    
    /*public boolean isManagerOfFactory(int managerId, int factoryId) {
        loadManagers(contextPath);
        
        // Pronaći managera sa datim ID-om
        Manager manager = findManager(managerId);
        
        // Provjeriti da li je pronađeni manager zadužen za traženu fabriku
        if (manager != null && manager.getFactoryId() == factoryId) {
            return true;
        }
        
        return false;
    }*/
    public boolean isManagerOfFactory(String username, int factoryId) {
        loadManagers(contextPath);
        
        // Pronaći managera sa datim username-om
        Manager manager = findManagerByUsername(username);
        
        // Provjeriti da li je pronađeni manager zadužen za traženu fabriku
        if (manager != null && manager.getFactoryId() == factoryId) {
            return true;
        }
        
        return false;
    }

    private Manager findManagerByUsername(String username) {
        loadManagers(contextPath);
        for (Manager manager : managers) {
            if (manager.getUsername().equals(username)) {
                return manager;
            }
        }
        return null;
    }

    
    public ArrayList<Manager> findAvailableManagers(){
    	loadManagers(contextPath);
    	availableManagers = new ArrayList<>();
    	for(Manager manager : managers) {
    		if(manager.getFactoryId() == -1) {
    			availableManagers.add(manager);
    		}
    	}
    	
    	return availableManagers;
    }
    
    public Manager update(User userEdit) {
	    System.out.println(contextPath);
		loadManagers(contextPath); // Ensure users are loaded
	    for (Manager manager : managers) {
	        if (manager.getId() == userEdit.getId()) {
	        	manager.setUsername(userEdit.getUsername());
	        	manager.setPassword(userEdit.getPassword());
	        	manager.setBirthDate(userEdit.getBirthDate());
	        	manager.setGender(userEdit.getGender());
	        	manager.setName(userEdit.getName());
	        	manager.setSurname(userEdit.getSurname());
	            saveAllManagers();
	    		loadManagers(contextPath);
	            return manager;
	            } 
	    }
	    
	    System.out.println("Manager not found: " + userEdit.getUsername());
	    return null;
	}

    public Manager save(User user, String contextPath) {
        loadManagers(contextPath);
		Manager manager = new Manager(user.getId(), user.getUsername(), user.getPassword(), user.getName(), user.getSurname(), user.getGender(), user.getBirthDate(), user.getRole(), user.isBlocked(), -1);
        try {
            String filePath = contextPath + "managers.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(manager.getId() + ";" +
                    manager.getUsername() + ";" +
                    manager.getPassword() + ";" +
                    manager.getName() + ";" +
                    manager.getSurname() + ";" +
                    manager.getGender() + ";" +
                    manager.getBirthDate().format(formatter) + ";" +
                    manager.getRole() + ";" +
                    manager.getFactoryId() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println(manager.getUsername());
        return manager; // Return the saved Manager object
    }
    
    public Manager updateManager(Manager manager) {
		loadManagers(contextPath);
		Manager m = findManager(manager.getId());
		if(m != null) {
			m.setUsername(manager.getUsername());
			m.setPassword(manager.getPassword());
			m.setName(manager.getName());
			m.setSurname(manager.getSurname());
			m.setGender(manager.getGender());
			m.setBirthDate(manager.getBirthDate());
			m.setRole(manager.getRole());
			m.setFactoryId(manager.getFactoryId());
			saveAllManagers();
			return m;
		}
		else {
			return null;
		}
	}
    
    private void saveAllManagers() {
	    try {
	        String filePath = contextPath + "managers.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Manager manager : managers) {
	            bufferedWriter.write(manager.getId() + ";" +
	            		manager.getUsername() + ";" +
	                    manager.getPassword() + ";" +
	                    manager.getName() + ";" +
	                    manager.getSurname() + ";" +
	                    manager.getGender() + ";" +
	                    manager.getBirthDate().format(formatter) + ";" +
	                    manager.getRole() + ";" +
	                    manager.getFactoryId() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
    public Manager findManager(int id) {
		loadManagers(contextPath);
		for (Manager manager : managers) {
			if (manager.getId() == id) {
				return manager;
			}
		}
		return null;
	}

    private void loadManagers(String contextPath) {
        this.managers = new ArrayList<Manager>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/managers.txt");
            System.out.println("Loading managers from: " + file.getAbsolutePath());
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
                int factoryId = Integer.parseInt(st.nextToken().trim());
                managers.add(new Manager(id, username, password, name, surname, gender, birthDate, role, blocked, factoryId));
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
}
