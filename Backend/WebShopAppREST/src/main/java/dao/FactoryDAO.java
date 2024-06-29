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
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

public class FactoryDAO {
	
	private ArrayList<Factory> factories = new ArrayList<>();
	private ChocolateDAO chocolateDAO;
	private LocationDAO locationDAO;
	private String contextPath;

	
	public FactoryDAO(String contextPath) {
		this.contextPath = contextPath;
		chocolateDAO = new ChocolateDAO(contextPath);
		locationDAO = new LocationDAO(contextPath);
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
		loadFactories(contextPath);
		loadChocolatesForFactories();
		for(Factory f: factories) {
			f.loadImageString();
		}
		return factories;
	}

	public Factory findFactory(int id) {
		loadFactories(contextPath);
		loadChocolatesForFactories();
		for (Factory factory : factories) {
			if (factory.getId() == id) {
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