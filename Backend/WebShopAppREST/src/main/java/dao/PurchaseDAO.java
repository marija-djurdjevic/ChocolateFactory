package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.Chocolate;
import beans.Purchase;
import beans.enums.PurchaseStatus;

public class PurchaseDAO {
    private ArrayList<Purchase> purchases = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String contextPath;
    private ChocolateDAO chocolateDAO;

    public PurchaseDAO(String contextPath) {
        this.contextPath = contextPath;
        loadPurchases(contextPath);
        chocolateDAO = new ChocolateDAO(contextPath);
    }

    public ArrayList<Purchase> findAll() {
        loadPurchases(contextPath);
        return purchases;
    }

    public Purchase save(Purchase purchase) {
        loadPurchases(contextPath);
        int maxId = 0;
        for (Purchase p : purchases) {
            int currentId = Integer.parseInt(p.getId()); // Assuming IDs are of format P1, P2, P3, etc.
            if (currentId > maxId) {
                maxId = currentId;
            }
        }
        maxId++;
        purchase.setId("P" + maxId);

        try {
            String filePath = contextPath + "purchases.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(purchase.getId() + ";" +
                    serializeChocolates(purchase.getChocolates()) + ";" +
                    purchase.getFactoryId() + ";" +
                    purchase.getDateAndTime().format(formatter) + ";" +
                    purchase.getPrice() + ";" +
                    purchase.getCustomerName() + ";" +
                    purchase.getCustomerSurname() + ";" +
                    purchase.getStatus() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return purchase; // Return the saved Purchase object
    }

    private String serializeChocolates(ArrayList<Chocolate> chocolates) {
        StringBuilder sb = new StringBuilder();
        for (Chocolate chocolate : chocolates) {
            sb.append(chocolate.getId()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        }
        return sb.toString();
    }

    private ArrayList<Chocolate> deserializeChocolates(String chocolatesString) {
        ArrayList<Chocolate> chocolates = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(chocolatesString, ",");
        while (tokenizer.hasMoreTokens()) {
            int chocolateId = Integer.parseInt(tokenizer.nextToken().trim());
            // Assuming there's a method to find Chocolate by ID
            Chocolate chocolate = chocolateDAO.findChocolate(chocolateId);
            if (chocolate != null) {
                chocolates.add(chocolate);
            }
        }
        return chocolates;
    }

    private Chocolate findChocolateById(int chocolateId) {
        // Implement your logic to find Chocolate by ID here
        // You can use a ChocolateDAO method or any other approach to fetch Chocolate by ID
        return null; // Placeholder
    }
    
    public ArrayList<Purchase> getPurchasesByFactoryId(int factoryId) {
        ArrayList<Purchase> factoryPurchases = new ArrayList<>();
        for (Purchase purchase : purchases) {
            if (purchase.getFactoryId() == factoryId) {
                factoryPurchases.add(purchase);
            }
        }
        return factoryPurchases;
    }

    private void loadPurchases(String contextPath) {
        this.purchases = new ArrayList<>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/purchases.txt");
            in = new BufferedReader(new FileReader(file));
            String line;
            StringTokenizer st;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                st = new StringTokenizer(line, ";");
                String id = st.nextToken().trim();
                ArrayList<Chocolate> chocolates = deserializeChocolates(st.nextToken().trim());
                int factoryId = Integer.parseInt(st.nextToken().trim());
                LocalDateTime dateAndTime = LocalDateTime.parse(st.nextToken().trim(), formatter);
                double price = Double.parseDouble(st.nextToken().trim());
                String customerName = st.nextToken().trim();
                String customerSurname = st.nextToken().trim();
                PurchaseStatus status = PurchaseStatus.valueOf(st.nextToken().trim());
                this.purchases.add(new Purchase(id, chocolates, factoryId, dateAndTime, price, customerName, customerSurname, status));
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
