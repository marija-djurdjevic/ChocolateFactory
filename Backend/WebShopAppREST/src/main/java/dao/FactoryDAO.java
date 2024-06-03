package dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.Factory;
import beans.Location;

public class FactoryDAO {
	
	private ArrayList<Factory> factories = new ArrayList<>();
	
	public FactoryDAO() {
		
		factories.add(new Factory(1, "test", 5));
	}
	
	public FactoryDAO(String contextPath) {
		loadFactories(contextPath);
		factories.add(new Factory(1, "test", 5));
		}

	public ArrayList<Factory> findAll() {
		return factories;
	}

	public Factory findFactory(int id) {
		for (Factory factory : factories) {
			if (factory.getId() == id) {
				return factory;
			}
		}
		return null;
	}
	
	public Factory updateFactory(int id, Factory factory) {
		Factory f = findFactory(id);
		if (f == null) {
			return save(factory);
		} else {
			f.setName(factory.getName());
			f.setWorktime(factory.getWorktime());
			f.setStatus(factory.isStatus());
			f.setLocation(factory.getLocation());
			f.setImage(factory.getImage());
			f.setGrade(factory.getGrade());
			return f;
		}
	}
	
	public Factory save(Factory factory) {
		int maxId = -1;
		for (Factory f : factories) {
			if (f.getId() > maxId) {
				maxId = f.getId();
			}
		}
		maxId++;
		factory.setId(maxId);
		factories.add(factory);
		return factory;
	}
	
	public Factory deleteFactoryById(int id) {
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
        // Ovdje implementirati logiku za pronalaženje filtriranih tvornica
        // Na primjer, možete filtrirati tvornice prema nekom kriteriju
        // Ovdje ćemo samo vratiti sve tvornice za demonstraciju
        return factories;
    }

	private void loadFactories(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/factories.txt");
			System.out.println(file.getCanonicalPath());
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
				String latitude = st.nextToken().trim();
				String longitude = st.nextToken().trim();
				String address = st.nextToken().trim();
				Location location = new Location(latitude, longitude, address);
				String image = st.nextToken().trim();
				double grade = Double.parseDouble(st.nextToken().trim());
				factories.add(new Factory(id, name, new ArrayList<>(), worktime, status, location, image, grade));
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
