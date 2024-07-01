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
import beans.roles.Worker;
import beans.User;
import beans.enums.Role;

public class WorkerDAO {
    private ArrayList<Worker> workers = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String contextPath;

    public WorkerDAO(String contextPath) {
        loadWorkers(contextPath);
        this.contextPath = contextPath;
    }

    public ArrayList<Worker> findAll() {
        loadWorkers(contextPath);
        return workers;
    }
    
    public Worker update(User userEdit) {
		loadWorkers(contextPath); 
		System.out.println(userEdit.getUsername());
		System.out.println(userEdit.getId());
	    for (Worker worker : workers) {
	        if (worker.getId() == userEdit.getId()) {
	        	System.out.println("pronadjen worker");
	        	worker.setUsername(userEdit.getUsername());
	        	worker.setPassword(userEdit.getPassword());
	        	worker.setBirthDate(userEdit.getBirthDate());
	        	worker.setGender(userEdit.getGender());
	        	worker.setName(userEdit.getName());
	        	worker.setSurname(userEdit.getSurname());
	            saveAllWorkers();
	    		loadWorkers(contextPath);
	            return worker;
	            } 
	    }
	    
	    System.out.println("User not found: " + userEdit.getUsername());
	    return null;
	}
    
    private void saveAllWorkers() {
	    try {
	        String filePath = contextPath + "workers.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Worker worker : workers) {
	            String roleString = worker.getRole().toString();
	        	bufferedWriter.write(worker.getId() + ";" +
	        			worker.getUsername() + ";" +
	        			worker.getPassword() + ";" +
	        			worker.getName() + ";" +
	        			worker.getSurname() + ";" +
	        			worker.getGender() + ";" +
	        			worker.getBirthDate().format(formatter) + ";" +
	                    roleString + ";" +
	                    worker.getFactoryId() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

    public Worker save(Worker worker, String contextPath) {
        loadWorkers(contextPath);

        try {
            String workersFilePath = contextPath + "/workers.txt";
            String usersFilePath = contextPath + "/users.txt";

            // Write to workers.txt
            FileWriter workersWriter = new FileWriter(workersFilePath, true);
            worker.setRole(Role.Worker);
            
            BufferedWriter workersBufferedWriter = new BufferedWriter(workersWriter);
            String roleString1 = worker.getRole().toString();
            workersBufferedWriter.write(worker.getId() + ";" +
                    worker.getUsername() + ";" +
                    worker.getPassword() + ";" +
                    worker.getName() + ";" +
                    worker.getSurname() + ";" +
                    worker.getGender() + ";" +
                    worker.getBirthDate().format(formatter) + ";" +
                    roleString1 + ";" +
                    worker.getFactoryId() + "\n");
            workersBufferedWriter.flush();
            workersBufferedWriter.close();

            // Write to users.txt
            FileWriter usersWriter = new FileWriter(usersFilePath, true);
            FileWriter writer = new FileWriter(usersFilePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            worker.setRole(Role.Worker);
            String roleString = worker.getRole().toString();
            bufferedWriter.write(worker.getId() + ";" +
                    worker.getUsername() + ";" +
                    worker.getPassword() + ";" +
                    worker.getName() + ";" +
                    worker.getSurname() + ";" +
                    worker.getGender() + ";" +
                    worker.getBirthDate().format(formatter) + ";" +
                    roleString + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return worker; // Return the saved Worker object
    }


    private void loadWorkers(String contextPath) {
        this.workers = new ArrayList<Worker>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/workers.txt");
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
                int factoryId = Integer.parseInt(st.nextToken().trim());
                workers.add(new Worker(id, username, password, name, surname, gender, birthDate, role, factoryId));
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
