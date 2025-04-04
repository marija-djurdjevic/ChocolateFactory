package dao;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import beans.Purchase;
import beans.ShoppingCart;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.stream.Collectors;
import beans.CartChocolate;
import beans.Chocolate;
import beans.enums.PurchaseStatus;
import beans.roles.Customer;

public class PurchaseDAO {
    private ArrayList<Purchase> purchases = new ArrayList<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final String contextPath;
    private ChocolateDAO chocolateDAO;
    private CustomerDAO customerDAO;
	private CartChocolateDAO cartChocolateDAO;
	private ShoppingCartDAO shoppingCartDAO;

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
    
    public int countCancelledPurchasesByCustomerId(int customerId) {
        int count = 0;
        for (Purchase purchase : purchases) {
            if (purchase.getCustomerId() == customerId && purchase.getStatus() == PurchaseStatus.Canceled) {
                count++;
            }
        }
        return count;
    }
    public List<Customer> findSuspiciousCustomers() {
        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);
        return findAll().stream()
            .filter(purchase -> purchase.getStatus() == PurchaseStatus.Canceled)
            .collect(Collectors.groupingBy(Purchase::getCustomerId, Collectors.counting()))
            .entrySet().stream()
            .filter(entry -> entry.getValue() > 5)
            .map(entry -> customerDAO.findById(entry.getKey()))
            .collect(Collectors.toList());
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
    
    public ArrayList<Purchase> findCustomersPurchases(int customerId) {   	
        loadPurchases(contextPath);
        ArrayList<Purchase> filteredPurchases = new ArrayList<Purchase>();
        for(Purchase purchase : purchases) {
        	if(purchase.getCustomerId() == customerId) {
        		filteredPurchases.add(purchase);
        	}
        }
        
        return filteredPurchases;
    }
    
    private void updateCartChocolates(Purchase purchase, ShoppingCart shoppingCart) {
    	cartChocolateDAO.updateCartPurchase(purchase, shoppingCart);		
    }
    
    public Purchase cancel(Purchase p) {
        loadPurchases(contextPath);
        for (Purchase purchase : purchases) {
            if (purchase.getId().equals(p.getId())) {
                purchase.setStatus(PurchaseStatus.Canceled);
                customerDAO.updatePointsMinus(p.getCustomerId(), p.getPrice());
                updateCanceledChocolatesAmounts(p);
                saveAll();
                return purchase;
            }
        }
        return null;
    }
    
    private void updateCanceledChocolatesAmounts(Purchase purchase) {
		for(CartChocolate cc : cartChocolateDAO.findAll()) {
			if(purchase.getId().equals(cc.getPurchaseId())) {
				System.out.println("USAO NASAO PROSAO");
				Chocolate chocolate = chocolateDAO.findChocolate(cc.getChocolateId());
				int newAmount = chocolate.getAmountOfChocolate() + cc.getAmount();
				chocolate.setAmountOfChocolate(newAmount);
			}
		}
		
		chocolateDAO.saveAllChocolates();
    }
    
    public void saveAll() {
		try {
	        String filePath = contextPath + "purchases.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (Purchase purchase : purchases) {
	        	bufferedWriter.write(purchase.getId() + ";" +
	                    purchase.getFactoryId() + ";" +
	                    purchase.getDateAndTime().format(formatter) + ";" +
	                    purchase.getPrice() + ";" +
	                    purchase.getCustomerId() + ";" +
	                    purchase.getStatus() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
    
    public List<Purchase> findByFactoryId(int factoryId) {
        List<Purchase> filteredPurchases = new ArrayList<>();
        for (Purchase purchase : purchases) {
            if (purchase.getFactoryId() == factoryId) {
                filteredPurchases.add(purchase);
            }
        }
        return filteredPurchases;
    }
   public List<Customer> findCustomersByFactoryId(int factoryId) {
        List<Purchase> purchases = findByFactoryId(factoryId);
        Set<Customer> customers = new HashSet<>();
        for (Purchase purchase : purchases) {
            Customer customer = customerDAO.findById(purchase.getCustomerId());
            if (customer != null) {
                customers.add(customer);
            }
        }
        return new ArrayList<>(customers);
    }
   
   public Purchase findPurchaseById(String id) {
	    for (Purchase purchase : purchases) {
	        if (purchase.getId().equals(id)) {
	            return purchase;
	        }
	    }
	    return null;
	}
   
   public void updatePurchase(Purchase purchase) {
	    loadPurchases(contextPath);
	    
	    for (int i = 0; i < purchases.size(); i++) {
	        if (purchases.get(i).getId().equals(purchase.getId())) {
	            purchases.set(i, purchase);
	            break;
	        }
	    }

	    savePurchases(contextPath);
	}
   
   private void savePurchases(String contextPath) {
	    BufferedWriter writer = null;
	    try {
	        File file = new File(contextPath + "/purchases.txt");
	        writer = new BufferedWriter(new FileWriter(file));

	        for (Purchase purchase : purchases) {
	            writer.write(purchase.getId() + ";" +
	                         purchase.getFactoryId() + ";" +
	                         purchase.getDateAndTime().format(formatter) + ";" +
	                         purchase.getPrice() + ";" +
	                         purchase.getCustomerId() + ";" +
	                         purchase.getStatus() + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (writer != null) {
	            try {
	                writer.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}

    /*public List<Customer> findCustomersByFactoryId(int factoryId) {
        List<Purchase> purchases = findByFactoryId(factoryId);
        List<Customer> customers = new ArrayList<>();
        
        for (Purchase purchase : purchases) {
            Customer customer = customerDAO.findById(purchase.getCustomerId());
            if (customer != null) {
                customers.add(customer);
            }
        }
        
        return customers;
    }*/


   // private String serializeChocolates(ArrayList<Chocolate> chocolates) {
     //   StringBuilder sb = new StringBuilder();
       // for (Chocolate chocolate : chocolates) {
         //   sb.append(chocolate.getId()).append(",");
        //}
        //if (sb.length() > 0) {
          //  sb.deleteCharAt(sb.length() - 1); // Remove the last comma
        //}
        //return sb.toString();
    //}

    //private ArrayList<Chocolate> deserializeChocolates(String chocolatesString) {
      //  ArrayList<Chocolate> chocolates = new ArrayList<>();
        //StringTokenizer tokenizer = new StringTokenizer(chocolatesString, ",");
        //while (tokenizer.hasMoreTokens()) {
          //  int chocolateId = Integer.parseInt(tokenizer.nextToken().trim());
            // Assuming there's a method to find Chocolate by ID
           // Chocolate chocolate = chocolateDAO.findChocolate(chocolateId);
            //if (chocolate != null) {
              //  chocolates.add(chocolate);
            //}
        //}
        //return chocolates;
    //}
    
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
					if(purchase.getId().equals(cc.getPurchaseId())) {
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

    public List<Purchase> loadPurchasesWithChocolatesForFactory(int factoryId) {
        chocolateDAO = new ChocolateDAO(contextPath);
        cartChocolateDAO = new CartChocolateDAO(contextPath);
        List<Purchase> factoryPurchases = new ArrayList<>();

        for (Purchase purchase : purchases) {
            if (purchase.getFactoryId() == factoryId) {
                double price = 0;
                purchase.chocolates = new ArrayList<>();

                for (CartChocolate cc : cartChocolateDAO.findAll()) {
                    if (purchase.getId().equals(cc.getPurchaseId())) {
                        Chocolate chocolate = chocolateDAO.findChocolate(cc.getChocolateId());
                        if (chocolate != null) {
                            price += chocolate.getPrice() * cc.getAmount();
                            if (!purchase.containsChocolateWithId(chocolate.getId())) {
                                purchase.chocolates.add(chocolate);
                            }
                        }
                    }
                }

                purchase.setPrice(price);
                factoryPurchases.add(purchase);
            }
        }

        return factoryPurchases;
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
