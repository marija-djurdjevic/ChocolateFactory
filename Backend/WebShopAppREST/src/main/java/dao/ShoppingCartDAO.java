package dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import beans.CartChocolate;
import beans.Chocolate;
import beans.Location;
import beans.ShoppingCart;

public class ShoppingCartDAO {
	private ArrayList<ShoppingCart> shoppingCarts = new ArrayList<>();
	private ChocolateDAO chocolateDAO;
	private CartChocolateDAO cartChocolatesDAO;
	private String contextPath;
	
	public ShoppingCartDAO(String contextPath) {
		this.contextPath = contextPath;
		cartChocolatesDAO = new CartChocolateDAO(contextPath);
		loadShoppingCarts(contextPath);
	}

	public void loadChocolatesAndPricesToCart(ShoppingCart sc) {
		chocolateDAO = new ChocolateDAO(contextPath);
		for(Chocolate chocolate : chocolateDAO.findAll()) {
			System.out.println(chocolate);
		}
		for(ShoppingCart shoppingCart : shoppingCarts) {
			if(sc.getId() == shoppingCart.getId()) {
				System.out.println("NASAO ODGOVARAJUCI SHOPPING CART");
				double price = 0;
				shoppingCart.chocolates = new ArrayList<>();
				for(CartChocolate cc : cartChocolatesDAO.findAll()) {
					System.out.println(cc);
					if(shoppingCart.getId() == cc.getShoppingCartId()) {
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
						if (!shoppingCart.containsChocolateWithId(chocolate.getId())) {
							shoppingCart.chocolates.add(chocolate);
							System.out.println("DODAO COKOLADU U SOP CART");
							System.out.println(chocolate); 
						}
						
					}
				}
				
				shoppingCart.setPrice(price);
				System.out.println(shoppingCart);
			}
		}
	}	
	
		
	
	public ArrayList<ShoppingCart> findAll() {
		loadShoppingCarts(contextPath);
		for(ShoppingCart sc : shoppingCarts) {
			loadChocolatesAndPricesToCart(sc);
		}
		return shoppingCarts;
	}
	
	public ShoppingCart deleteByCustomerId(int id) {
		loadShoppingCarts(contextPath);
		ShoppingCart cartToRemove = null;
		for(ShoppingCart shoppingCart : shoppingCarts) {
			if(shoppingCart.getCustomerId() == id) {
				System.out.println("nasao koga treba obrisati");
				cartToRemove = shoppingCart;
	             break;
			}
		}
		if (cartToRemove != null) {
			shoppingCarts.remove(cartToRemove.getId());
			System.out.println("obrisao");
			saveAll();
			return cartToRemove;
		}
		return null;
	}

	public ShoppingCart findShoppingCart(int customerId) {
		loadShoppingCarts(contextPath);
		for(ShoppingCart shoppingCart : shoppingCarts) {
			loadChocolatesAndPricesToCart(shoppingCart);
		}
		for (ShoppingCart cart : shoppingCarts) {
			System.out.println(cart.getCustomerId() + "i " + customerId);
			if (cart.getCustomerId() == customerId) {
				System.out.println("nasao shopping cart");
				System.out.println(cart);
				return cart;
			}
		}
		return null;
	}
	
	public ArrayList<CartChocolate> compatibileCartChocolates(ShoppingCart sc) {
		return cartChocolatesDAO.findCustomersCartChocolates(sc.getId());
	}
	
	public ShoppingCart updateShoppingCart(ShoppingCart shoppingCart) {
		loadShoppingCarts(contextPath);
		loadChocolatesAndPricesToCart(shoppingCart);
		saveAll();
		for(ShoppingCart sc : shoppingCarts) {
			if(sc.getId() == shoppingCart.getId()) {
				return sc;
			}
		}
		
		return null;
	}
	
	public ShoppingCart save(ShoppingCart shoppingCart) {
		loadShoppingCarts(contextPath);
		for(ShoppingCart sc : shoppingCarts) {
			System.out.println(sc);
		}
        int maxId = -1;
        for (ShoppingCart sc : shoppingCarts) {
            if (sc.getId() > maxId) {
                maxId = sc.getId();
            }
        }
        maxId++;
        shoppingCart.setId(maxId);
        
        try {
            String filePath = contextPath + "shoppingCarts.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(shoppingCart.getId() + ";" +
                    shoppingCart.getCustomerId() + ";" +
            		shoppingCart.getPrice() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return shoppingCart; // Return the saved Chocolate object
	}
	
	public ShoppingCart deleteCartById(int id) {
        ShoppingCart cartToRemove = null;
        for (ShoppingCart shoppingCart : shoppingCarts) {
            if (shoppingCart.getId() == id) {
            	cartToRemove = shoppingCart;
                break;
            }
        }
        if (cartToRemove != null) {
        	shoppingCarts.remove(cartToRemove);
        }
        
        saveAll();
        return cartToRemove;
    }

	public void saveAll() {
		try {
	        String filePath = contextPath + "shoppingCarts.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (ShoppingCart shoppingCart : shoppingCarts) {
	            bufferedWriter.write(shoppingCart.getId() + ";" +
	                    shoppingCart.getCustomerId() + ";" +
	                    shoppingCart.getPrice() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	private void loadShoppingCarts(String contextPath) {
		this.shoppingCarts = new ArrayList<ShoppingCart>();
		int i = 0;
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/shoppingCarts.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				int id = Integer.parseInt(st.nextToken().trim());
				int customerId = Integer.parseInt(st.nextToken().trim());
				double price = Double.parseDouble(st.nextToken().trim());
				System.out.println("ucitao jedan");
				shoppingCarts.add(new ShoppingCart(id, customerId, new ArrayList<Chocolate>(), price));
				i++;
				
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
		
		System.out.println("i ======= " + i);
	}
}
