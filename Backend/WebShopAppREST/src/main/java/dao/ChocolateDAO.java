package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.Chocolate;
import beans.Factory;

public class ChocolateDAO {

	private ArrayList<Chocolate> chocolates = new ArrayList<>();
	
	public ChocolateDAO(String contextPath) {
		loadChocolates(contextPath);
	}

	public ArrayList<Chocolate> findAll() {
		return chocolates;
	}

	public Chocolate findChocolate(int id) {
		for (Chocolate chocolate : chocolates) {
			if (chocolate.getId() == id) {
				return chocolate;
			}
		}
		return null;
	}
	
	public Chocolate updateChocolate(int id, Chocolate chocolate, String contextPath) {
		Chocolate c = findChocolate(id);
		if (c == null) {
			return save(chocolate, contextPath);
		} else {
			c.setName(chocolate.getName());
			c.setChocolateSort(chocolate.getChocolateSort());
			c.setFactoryId(chocolate.getFactoryId());
			c.setChocolateType(chocolate.getChocolateType());
			c.setGramsOfChocolate(chocolate.getGramsOfChocolate());
			c.setChocolateDescription(chocolate.getChocolateDescription());
			c.setImagePath(chocolate.getImagePath());
			c.setAvailable(chocolate.isAvailable());
			c.setAmountOfChocolate(chocolate.getAmountOfChocolate());
			return c;
		}
	}
	
	public Chocolate save(Chocolate chocolate, String contextPath) {
        int maxId = -1;
        for (Chocolate c : chocolates) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        maxId++;
        chocolate.setId(maxId);
        chocolate.setAvailable(false);
        chocolate.setAmountOfChocolate(0);
        try {
            String filePath = contextPath + "chocolates.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(chocolate.getId() + ";" +
                    chocolate.getName() + ";" +
            		chocolate.getPrice() + ";" +
                    chocolate.getChocolateSort() + ";" +
                    chocolate.getFactoryId() + ";" +
                    chocolate.getChocolateType() + ";" +
                    chocolate.getGramsOfChocolate() + ";" +
                    chocolate.getChocolateDescription() + ";" +
                    chocolate.getImagePath() + ";" +
                    chocolate.isAvailable() + ";" +
                    chocolate.getAmountOfChocolate() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        FactoryChocolateDAO dao = new FactoryChocolateDAO();
        dao.save(chocolate.getFactoryId(), chocolate.getId(), contextPath);
        return chocolate; // Return the saved Chocolate object
        
    }
	
	public Chocolate deleteChocolateById(int id) {
        Chocolate chocolateToRemove = null;
        for (Chocolate chocolate : chocolates) {
            if (chocolate.getId() == id) {
                chocolateToRemove = chocolate;
                break;
            }
        }
        if (chocolateToRemove != null) {
            chocolates.remove(chocolateToRemove);
        }
        return chocolateToRemove;
    }

	private void loadChocolates(String contextPath) {
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/chocolates.txt");
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
				double price = Double.parseDouble(st.nextToken().trim());
				String chocolateSort = st.nextToken().trim();
				int factoryId = Integer.parseInt(st.nextToken().trim());
				String chocolateType = st.nextToken().trim();
				int gramsOfChocolate = Integer.parseInt(st.nextToken().trim());
				String chocolateDescription = st.nextToken().trim();
				String imagePath = st.nextToken().trim();
				boolean isAvailable = Boolean.parseBoolean(st.nextToken().trim());
				int amountOfChocolate = Integer.parseInt(st.nextToken().trim());
				chocolates.add(new Chocolate(id, name, price, chocolateSort, factoryId, chocolateType, gramsOfChocolate, chocolateDescription, imagePath, isAvailable, amountOfChocolate));
				
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
