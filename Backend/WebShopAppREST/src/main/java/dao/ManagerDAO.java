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

import beans.roles.Manager;
import beans.enums.Role;

public class ManagerDAO {
    private ArrayList<Manager> managers = new ArrayList<>();
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

    public Manager save(Manager manager, String contextPath) {
        loadManagers(contextPath);

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

        return manager; // Return the saved Manager object
    }

    private void loadManagers(String contextPath) {
        this.managers = new ArrayList<Manager>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/managers.txt");
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
                managers.add(new Manager(id, username, password, name, surname, gender, birthDate, role, factoryId));
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
