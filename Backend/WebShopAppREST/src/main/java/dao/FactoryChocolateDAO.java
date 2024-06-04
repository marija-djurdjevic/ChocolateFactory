package dao;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import beans.Chocolate;
import beans.FactoryChocolate;

public class FactoryChocolateDAO {

	private ArrayList<FactoryChocolate> factorychocolates = new ArrayList<>();
	public FactoryChocolate save(int Factoryid, int Chocolateid, String contextPath) {
		FactoryChocolate factoryChocolate = new FactoryChocolate();
        int maxId = -1;
        for (FactoryChocolate fc : factorychocolates) {
            if (fc.getId() > maxId) {
                maxId = fc.getId();
            }
        }
        maxId++;
        factoryChocolate.setId(maxId);
        factoryChocolate.setFactoryId(Factoryid);
        factoryChocolate.setChocolateId(Chocolateid);
        try {
            String filePath = contextPath + "factorychocolates.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(factoryChocolate.getId() + ";" +
            		factoryChocolate.getFactoryId() + ";" +
            		factoryChocolate.getChocolateId() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return factoryChocolate; // Return the saved Chocolate object
    }
	
}
