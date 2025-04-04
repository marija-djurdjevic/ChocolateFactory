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

import beans.User;
import beans.enums.Role;
import beans.roles.Manager;

public class UserDAO {
    private ArrayList<User> users = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String contextPath;
    private WorkerDAO workerDAO;
    private ManagerDAO managerDAO;
    private CustomerDAO customerDAO;

    public UserDAO(String contextPath) {
        loadUsers(contextPath);
        this.contextPath = contextPath;
        workerDAO = new WorkerDAO(contextPath);
        managerDAO = new ManagerDAO(contextPath);
        customerDAO = new CustomerDAO(contextPath);
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
            String roleString = user.getRole().toString();
            bufferedWriter.write(user.getId() + ";" +
                    user.getUsername() + ";" +
                    user.getPassword() + ";" +
                    user.getName() + ";" +
                    user.getSurname() + ";" +
                    user.getGender() + ";" +
                    user.getBirthDate().format(formatter) + ";" +
                    roleString + ";" +
                    user.isBlocked() + "\n"); // Added blocked field
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return user; // Return the saved User object
    }

    public User saveCustomer(User user) {
        user.setRole(Role.Customer);
        User customerUser = save(user);
        CustomerDAO dao = new CustomerDAO(contextPath);
        dao.save(customerUser, contextPath);

        return customerUser;
    }

    public Manager saveManager(User user) {
        user.setRole(Role.Manager);
        User managerUser = save(user);
        ManagerDAO dao = new ManagerDAO(contextPath);
        Manager manager = dao.save(managerUser, contextPath);

        return manager;
    }

    private void loadUsers(String contextPath) {
        this.users = new ArrayList<>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/users.txt");
            System.out.println("Loading users from: " + file.getAbsolutePath());
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
                boolean blocked = Boolean.parseBoolean(st.nextToken().trim()); // Read blocked field
                User user = new User(id, username, password, name, surname, gender, birthDate, role, blocked); // Updated constructor
                users.add(user);
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
            System.out.println("Checking user: " + user.getUsername());
            if (user.getUsername().equals(username)) {
                System.out.println("Username matches for: " + username);
                if (user.getPassword().equals(password)) {
                    System.out.println("Password matches for: " + username);
                    return user;
                } else {
                    System.out.println("Password does not match for: " + username);
                }
            }
        }
        System.out.println("User not found: " + username);
        return null;
    }

    public User updateUser(User userEdit) {
        loadUsers(contextPath); // Ensure users are loaded
        System.out.println(contextPath);
        System.out.println(userEdit.getId());
        System.out.println(userEdit.getRole());
        for (User user : users) {
            System.out.println(user.getId());
            if (user.getId() == userEdit.getId()) {
                System.out.println("user id matches for: " + userEdit.getId());
                user.setUsername(userEdit.getUsername());
                user.setPassword(userEdit.getPassword());
                user.setBirthDate(userEdit.getBirthDate());
                user.setGender(userEdit.getGender());
                user.setName(userEdit.getName());
                user.setSurname(userEdit.getSurname());
                user.setBlocked(userEdit.isBlocked()); // Update blocked field

                if (user.getRole() == Role.valueOf("Administrator")) {
                    updateAdministrator(userEdit);
                } else if (user.getRole() == Role.valueOf("Customer")) {
                    System.out.println("jeste customer");
                    customerDAO.update(userEdit);
                } else if (user.getRole() == Role.valueOf("Manager")) {
                    System.out.println("jeste menadzer");
                    managerDAO.update(userEdit);
                } else if (user.getRole() == Role.valueOf("Worker")) {
                    System.out.println("jeste worker");
                    workerDAO.update(userEdit);
                } else {
                    System.out.println("invalid user");
                }

                saveAllUsers();
                loadUsers(contextPath);
                return user;
            }

        }
        System.out.println("User not found: " + userEdit.getUsername());
        return null;
    }

    public void updateAdministrator(User user) {
        // Logic for updating administrator
    }

    private void saveAllUsers() {
        try {
            String filePath = contextPath + "users.txt";
            FileWriter writer = new FileWriter(filePath, false);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            for (User user : users) {
                String roleString = user.getRole().toString();
                bufferedWriter.write(user.getId() + ";" +
                        user.getUsername() + ";" +
                        user.getPassword() + ";" +
                        user.getName() + ";" +
                        user.getSurname() + ";" +
                        user.getGender() + ";" +
                        user.getBirthDate().format(formatter) + ";" +
                        roleString + ";" +
                        user.isBlocked() + "\n"); // Added blocked field
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User authenticateUserFromUsername(String username) {
        loadUsers(contextPath); // Ensure users are loaded
        for (User user : users) {
            System.out.println("Checking user: " + user.getUsername());
            if (user.getUsername().equals(username)) {
                System.out.println("Username matches for: " + username);
                return user;
            }
        }
        System.out.println("User not found: " + username);
        return null;
    }
    
    public void blockUser(String username) {
        loadUsers(contextPath); // Ensure users are loaded
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setBlocked(true); // Block the user
                saveAllUsers(); // Save changes to the file
                break;
            }
        }
    }

    public void unblockUser(String username) {
        loadUsers(contextPath); // Ensure users are loaded
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                user.setBlocked(false); // Unblock the user
                saveAllUsers(); // Save changes to the file
                break;
            }
        }
    }
    public User findUserByUsername(String username) {
        loadUsers(contextPath); // Ensure users are loaded
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null; // Return null if user is not found
    }
}
