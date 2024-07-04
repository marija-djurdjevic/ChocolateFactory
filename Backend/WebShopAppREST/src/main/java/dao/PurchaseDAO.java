package dao;

import java.util.ArrayList;

import beans.Purchase;
import beans.ShoppingCart;

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
import java.util.UUID;

import beans.CartChocolate;
import beans.Chocolate;
import beans.Purchase;
import beans.enums.PurchaseStatus;

public class PurchaseDAO {
    private ArrayList<Purchase> purchases = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String contextPath;
    private ChocolateDAO chocolateDAO;
	private CartChocolateDAO cartChocolateDAO;
	private ShoppingCartDAO shoppingCartDAO;
	private CustomerDAO customerDAO;

    public PurchaseDAO(String contextPath) {
        this.contextPath = contextPath;
        loadPurchases(contextPath);
        chocolateDAO = new ChocolateDAO(contextPath);
        cartChocolateDAO = new CartChocolateDAO(contextPath);
        shoppingCartDAO = new ShoppingCartDAO(contextPath);
    	customerDAO = new CustomerDAO(contextPath);
    }

    public ArrayList<Purchase> findAll() {
        loadPurchases(contextPath);
        for(Purchase purchase : purchases) {
			loadChocolatesAndPriceToPurchase(purchase);
		}
        return purchases;
    }

    public Purchase save(Purchase purchase) {
        purchase.setDateAndTime(LocalDateTime.now());    
        loadPurchases(contextPath);
        String uniqueId;
        boolean isUnique;

        do {
            uniqueId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
            isUnique = true;

            for (Purchase p : purchases) {
                if (p.getId().equals(uniqueId)) {
                    isUnique = false;
                    break;
                }
            }
        } while (!isUnique);
        purchase.setId(uniqueId);
        
        try {
            System.out.println("Date and Time before format: " + purchase.getDateAndTime());
            System.out.println("Formatted Date and Time: " + purchase.getDateAndTime().format(formatter));
            
            purchase.setStatus(PurchaseStatus.Processing);
            String filePath = contextPath + "purchases.txt";
            FileWriter writer = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(purchase.getId() + ";" +
                    purchase.getFactoryId() + ";" +
                    purchase.getDateAndTime().format(formatter) + ";" +
                    purchase.getPrice() + ";" +
                    purchase.getCustomerId() + ";" +
                    purchase.getStatus() + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        ShoppingCart shoppingCart = shoppingCartDAO.deleteByCustomerId(purchase.getCustomerId());
        customerDAO.updatePoints(shoppingCart.getCustomerId(), shoppingCart.getPrice());
        updateCartChocolates(purchase, shoppingCart);
        return purchase;
    }
    
    private void updateCartChocolates(Purchase purchase, ShoppingCart shoppingCart) {
    	cartChocolateDAO.updateCartPurchase(purchase, shoppingCart);		
    }
    
    public void loadChocolatesAndPriceToPurchase(Purchase pc) {
		chocolateDAO = new ChocolateDAO(contextPath);
		for(Chocolate chocolate : chocolateDAO.findAll()) {
			System.out.println(chocolate);
		}
		for(Purchase purchase : purchases) {
			if(pc.getId() == purchase.getId()) {
				System.out.println("NASAO ODGOVARAJUCI SHOPPING CART");
				double price = 0;
				purchase.chocolates = new ArrayList<>();
				for(CartChocolate cc : cartChocolateDAO.findAll()) {
					System.out.println(cc);
					if(purchase.getId() == cc.getPurchaseId()) {
						System.out.println("NASAO CARTCHOCOLATE");
						Chocolate chocolate = chocolateDAO.findChocolate(cc.getChocolateId());
						if(chocolate == null) {
							System.out.println(cc.getChocolateId());
							for(Chocolate chocolateee : chocolateDAO.findAll()) {
								System.out.println(chocolateee);
							}
							System.out.println("*********************");
						}
						price += chocolate.getPrice() * cc.getAmount();
						System.out.println("CIJENA TRENUTNA" + price);
						if (!purchase.containsChocolateWithId(chocolate.getId())) {
							purchase.chocolates.add(chocolate);
							System.out.println("DODAO COKOLADU U SOP CART");
							System.out.println(chocolate); 
						}
						
					}
				}
				
				purchase.setPrice(price);
				System.out.println(purchase);
			}
		}
	}	

    private Chocolate findChocolateById(int chocolateId) {
        return null;
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
                int factoryId = Integer.parseInt(st.nextToken().trim());
                LocalDateTime dateAndTime = LocalDateTime.parse(st.nextToken().trim(), formatter);
                double price = Double.parseDouble(st.nextToken().trim());
                int customerId = Integer.parseInt(st.nextToken().trim());
                PurchaseStatus status = PurchaseStatus.valueOf(st.nextToken().trim());
                this.purchases.add(new Purchase(id, new ArrayList<Chocolate>(), factoryId, dateAndTime, price, customerId, status));
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
