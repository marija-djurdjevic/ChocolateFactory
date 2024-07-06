package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.StringTokenizer;

import beans.Chocolate;
import beans.Factory;
import beans.Location;
import beans.roles.Manager;
import beans.roles.Worker;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

public class FactoryDAO {
	
	private ArrayList<Factory> factories = new ArrayList<>();
	private ChocolateDAO chocolateDAO;
	private LocationDAO locationDAO;
    private WorkerDAO workerDAO;
    private ManagerDAO managerDAO;
    private UserDAO userDAO;
	private String contextPath;
	

	
	public FactoryDAO(String contextPath) {
		this.contextPath = contextPath;
		chocolateDAO = new ChocolateDAO(contextPath);
		locationDAO = new LocationDAO(contextPath);
		workerDAO = new WorkerDAO(contextPath);
		managerDAO = new ManagerDAO(contextPath);
		userDAO = new UserDAO(contextPath);
		loadFactories(contextPath);
		loadChocolatesForFactories();
	}
	
	private void loadChocolatesForFactories() {
		for (Factory factory : factories) {
			ArrayList<Chocolate> chocolates = chocolateDAO.findChocolatesByFactoryId(factory.getId());
			factory.setAvailableChocolates(chocolates);
		}
	}
	
	public ArrayList<Factory> findAll() {
		locationDAO.findAll();
		loadFactories(contextPath);
		loadChocolatesForFactories();
		userDAO.findAll();
		for(Factory f: factories) {
			f.loadImageString();
		}
		return factories;
	}
	

	public Chocolate updateChocolateAmountInFactory(int factoryId, int chocolateId, int newAmount) {
        loadFactories(contextPath);
        Factory factory = findFactory(factoryId);
        if (factory != null) {
            Chocolate chocolate = chocolateDAO.updateChocolateAmount(chocolateId, newAmount);
            if (chocolate != null) {
            	System.out.println(chocolate.getName());
                loadChocolatesForFactories(); 
                return chocolate;
            }
        }
        return null;
    }



	public Factory findFactory(int id) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		for (Factory factory : factories) {
			if (factory.getId() == id) {
				factory.loadImageString();
				return factory;
			}
		}
		return null;
	}
	
	public Chocolate addChocolateToFactory(int id, Chocolate chocolate) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		Factory f = findFactory(id);
		f.addChocolateToFactory(chocolate);
		return chocolate;
	}
	
	public Factory findFactoryByManagerId(String username) {
	    loadFactories(contextPath);
	    for (Factory factory : factories) {
	        if (managerDAO.isManagerOfFactory(username, factory.getId())) {
	        	System.out.println("dasdasdadsa");
	            factory.loadImageString();
	            return factory;
	        }
	    }
	    return null;
	}
	
	
	//public Factory updateFactory(int id, Factory factory) {
		//loadFactories(contextPath);
		//loadChocolatesForFactories();
		//Factory f = findFactory(id);
		//if (f == null) {
			//return save(factory);
		//} else {
			//f.setName(factory.getName());
			//f.setWorktime(factory.getWorktime());
			//f.setStatus(factory.isStatus());
			//f.setLocation(factory.getLocation());
			//f.setImage(factory.getImage());
			//f.setGrade(factory.getGrade());
			//return f;
		//}
	//}
	
	public Factory save(Factory factory) {
		loadFactories(contextPath);
        int maxId = -1;
        for (Factory f : factories) {
            if (f.getId() > maxId) {
                maxId = f.getId();
            }
        }
        maxId++;
        factory.setId(maxId);
        
        String path = this.contextPath + "images\\factory" + maxId + ".jpg"; 
        saveImage(path, factory.getImageString());
        factory.setImagePath(path);
        try {
            String filePath = contextPath + "factories.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(factory.getId() + ";" +
                    factory.getName() + ";" +
            		factory.getWorktime() + ";" +
                    factory.isStatus() + ";" +
                    factory.getLocation() + ";" +
                    factory.getImagePath() + ";" +
                    factory.getGrade() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return factory; // Return the saved Chocolate object
	}
	
	private void saveImage(String path, String imageString) {
		byte[] imageBytes = Base64.getDecoder().decode(imageString);
		try (FileOutputStream fos = new FileOutputStream(path)){
			fos.write(imageBytes);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public Factory deleteFactoryById(int id) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
        Factory factoryToRemove = null;
        for (Factory factory : factories) {
            if (factory.getId() == id) {
                factoryToRemove = factory;
                break;
            }
        }
        if (factoryToRemove != null) {
            factories.remove(factoryToRemove);
        }
        return factoryToRemove;
    }

    // Metoda za filtriranje tvornica prema statusu (radi ili ne radi)
    public ArrayList<Factory> filterFactoriesByStatus(boolean status) {
    	loadFactories(contextPath);
		loadChocolatesForFactories();
        ArrayList<Factory> filteredFactories = new ArrayList<>();
        for (Factory factory : factories) {
            if (factory.isStatus() == status) {
                filteredFactories.add(factory);
            }
        }
        return filteredFactories;
    }

    // Metoda za pronalaženje filtriranih tvornica
    public ArrayList<Factory> findFilteredFactories() {
    	loadFactories(contextPath);
		loadChocolatesForFactories();
        // Ovdje implementirati logiku za pronalaženje filtriranih tvornica
        // Na primjer, možete filtrirati tvornice prema nekom kriteriju
        // Ovdje ćemo samo vratiti sve tvornice za demonstraciju
        return factories;
    }
    public Worker addWorkerToFactory(int factoryId, Worker worker) {
        loadFactories(contextPath);
        loadChocolatesForFactories();
        
        Factory factory = findFactory(factoryId);
        if (factory != null) {
            worker.setFactoryId(factoryId);
            workerDAO.save(worker, contextPath); // Save the worker with updated factory ID
            return worker;
        }
        return null;
    }
    public boolean isManagerOfFactory(String username, int factoryId) {
        loadFactories(contextPath);
        
        // Pronaći managera sa datim username-om
        Manager manager = null;
        for (Manager m : managerDAO.findAll()) {
            if (m.getUsername().equals(username)) {
                manager = m;
                break;
            }
        }
        
        // Provjeriti da li je pronađeni manager zadužen za traženu fabriku
        if (manager != null && manager.getFactoryId() == factoryId) {
            return true;
        }
        
        return false;
    }
    public boolean isWorkerAtFactory(String username, int factoryId) {
        loadFactories(contextPath);
        
        // Pronaći managera sa datim username-om
        Worker worker = null;
        for (Worker w : workerDAO.findAll()) {
            if (w.getUsername().equals(username)) {
                worker = w;
                break;
            }
        }
        
        // Provjeriti da li je pronađeni manager zadužen za traženu fabriku
        if (worker != null && worker.getFactoryId() == factoryId) {
            return true;
        }
        
        return false;
    }
	private void loadFactories(String contextPath) {
		this.factories = new ArrayList<Factory>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/factories.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				String name = st.nextToken().trim();
				String worktime = st.nextToken().trim();
				boolean status = Boolean.parseBoolean(st.nextToken().trim());
				int locationId = Integer.parseInt(st.nextToken().trim());
				String imagePath = st.nextToken().trim();
				double grade = Double.parseDouble(st.nextToken().trim());
				factories.add(new Factory(id, name, new ArrayList<>(), worktime, status, locationId, imagePath, "", grade));
				
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