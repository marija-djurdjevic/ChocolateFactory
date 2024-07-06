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

public class ChocolateDAO {

	private ArrayList<Chocolate> chocolates = new ArrayList<>();
	
	private final String contextPath;
	
	public ChocolateDAO(String contextPath) {
		this.contextPath = contextPath;
		loadChocolates(contextPath);
	}

	public ArrayList<Chocolate> findAll() {
		loadChocolates(contextPath);
		for(Chocolate c: chocolates) {
			c.loadImageString();
		}
		return chocolates;
	}
	
	public Chocolate updateChocolateAmount(int chocolateId, int newAmount) {
        loadChocolates(contextPath);
        Chocolate chocolate = findChocolate(chocolateId);
        System.out.println("alooooooooooooo" + chocolate);
        if (chocolate != null) {
        	System.out.println("kolicina glupe najgluplje cokolade" + chocolate.getAmountOfChocolate());
            chocolate.setAmountOfChocolate(newAmount);
            if(newAmount > 0) 
            {
            	chocolate.setAvailable(true);
    		}
            else {
            	chocolate.setAvailable(false);
            }
            System.out.println(chocolate.getAmountOfChocolate());
            saveAllChocolates();
            return chocolate;
        }
        return null;
    }

	public Chocolate findChocolate(int id) {
		loadChocolates(contextPath);
		for (Chocolate chocolate : chocolates) {
			System.out.println("jedna po jedna " + chocolate);
			System.out.println("chocolate id " + chocolate.getId() + " i id  " + id);
			if (chocolate.getId() == id) {
				System.out.println("nasao prokletu cokoladu");
				chocolate.loadImageString();
				return chocolate;
			}
		}
		return null;
	}
	
	public Chocolate updateChocolate(Chocolate chocolate) {
		loadChocolates(contextPath);
		int izmjena = 0;
		Chocolate c = findChocolate(chocolate.getId());
		if(c != null) {
			c.setName(chocolate.getName());
			c.setPrice(chocolate.getPrice());
			c.setChocolateSort(chocolate.getChocolateSort());
			c.setFactoryId(chocolate.getFactoryId());
			c.setChocolateType(chocolate.getChocolateType());
			c.setGramsOfChocolate(chocolate.getGramsOfChocolate());
			c.setChocolateDescription(chocolate.getChocolateDescription());
			String path = this.contextPath + "images\\chocolate" + chocolate.getId() + izmjena + ".jpg"; 
            saveImage(path, chocolate.getImageString());
            c.setImagePath(path);
			c.setAvailable(chocolate.isAvailable());
			c.setAmountOfChocolate(chocolate.getAmountOfChocolate());
			saveAllChocolates();
			izmjena++;
			return c;
		}
		else {
			return null;
		}
	}
	
	public void saveAllChocolates() {
	    try {
	        String filePath = contextPath + "chocolates.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Chocolate chocolate : chocolates) {
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
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	 public ArrayList<Chocolate> findChocolatesByFactoryId(int factoryId) {
		 loadChocolates(contextPath);
	     ArrayList<Chocolate> result = new ArrayList<>();
	     	for (Chocolate chocolate : chocolates) {
	            if (chocolate.getFactoryId() == factoryId) {
	            	chocolate.loadImageString();
	                result.add(chocolate);
	            }
	        }
	     return result;
	    }

	
	public Chocolate save(Chocolate chocolate, String contextPath) {
		loadChocolates(contextPath);
        int maxId = -1;
        for (Chocolate c : chocolates) {
            if (c.getId() > maxId) {
                maxId = c.getId();
            }
        }
        maxId++;
        chocolate.setId(maxId);
        
        String path = this.contextPath + "images\\chocolate" + maxId + ".jpg"; 
        saveImage(path, chocolate.getImageString());
        chocolate.setImagePath(path);
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
        
        return chocolate; // Return the saved Chocolate object
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

	 public Chocolate deleteChocolateById(int id) {
	        loadChocolates(contextPath);
	        Chocolate chocolateToRemove = null;
	        for (Chocolate chocolate : chocolates) {
	            if (chocolate.getId() == id) {
	                chocolateToRemove = chocolate;
	                break;
	            }
	        }
	        if (chocolateToRemove != null) {
	            chocolates.remove(chocolateToRemove);
	            saveAllChocolates(); 
	        }
	        return chocolateToRemove;
	    }

	private void loadChocolates(String contextPath) {
		this.chocolates = new ArrayList<Chocolate>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/chocolates.txt");
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
				boolean available = Boolean.parseBoolean(st.nextToken().trim());
				int amountOfChocolate = Integer.parseInt(st.nextToken().trim());
				this.chocolates.add(new Chocolate(id, name, price, chocolateSort, factoryId, chocolateType, gramsOfChocolate, chocolateDescription, imagePath, "", available, amountOfChocolate));

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