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
import beans.Location;
import beans.roles.Manager;

public class CartChocolateDAO {
	private ArrayList<CartChocolate> cartChocolates = new ArrayList<>();
	private String contextPath;
	
	public CartChocolateDAO(String contextPath) {
		this.contextPath = contextPath;
		loadCartChocolates(contextPath);
	}

	public ArrayList<CartChocolate> findAll() {
		loadCartChocolates(contextPath);
		return cartChocolates;
	}
	
	public ArrayList<CartChocolate> findCustomersCartChocolates(int cartId) {
	    ArrayList<CartChocolate> concreteCartChocolates = new ArrayList<>();
		for(CartChocolate cc : cartChocolates) {
			if(cc.getShoppingCartId() == cartId) {
				concreteCartChocolates.add(cc);
			}
		}
		
		return concreteCartChocolates;
	}
	
	public CartChocolate save(CartChocolate cartChocolate) {
		loadCartChocolates(contextPath);
		for(CartChocolate cc : cartChocolates) {
			if(cc.getShoppingCartId() == cartChocolate.getShoppingCartId() && cc.getChocolateId() == cartChocolate.getChocolateId()) {
				int amountOld = cc.getAmount();
				cc.setAmount(amountOld + cartChocolate.getAmount());
				saveAll();
				return cc;
			}
		}
        try {
            String filePath = contextPath + "cartChocolates.txt"; // Use the provided path
            FileWriter writer = new FileWriter(filePath, true); // Open in append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(cartChocolate.getShoppingCartId() + ";" +
                    cartChocolate.getChocolateId() + ";" +
            		cartChocolate.getAmount() + "\n");
            bufferedWriter.flush(); // Ensure all data is written to the file
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return cartChocolate; // Return the saved Chocolate object
	}
	
	public void deleteCartChocolate(int cartId, int chocolateId) {
		loadCartChocolates(contextPath);
		for(CartChocolate cc : cartChocolates) {
			if(cc.getShoppingCartId() == cartId) {
				if(cc.getChocolateId() == chocolateId) {
					cartChocolates.remove(cc);
				}
			}
		}	
	}
	
	public void updateCartChocolateAmount(int cartId, int chocolateId) {
		loadCartChocolates(contextPath);
		for(CartChocolate cc : cartChocolates) {
			if(cc.getShoppingCartId() == cartId) {
				if(cc.getChocolateId() == chocolateId) {
					cc.setAmount(cc.getAmount() + 1);
				}
			}
		}	
	}
	
	public void deleteAll() {
		cartChocolates = new ArrayList<CartChocolate>();
		saveAll();
		
	}
	
	private void saveAll() {
		try {
	        String filePath = contextPath + "cartChocolates.txt";
	        FileWriter writer = new FileWriter(filePath, false); 
	        BufferedWriter bufferedWriter = new BufferedWriter(writer);
	        for (CartChocolate cartChocolate : cartChocolates) {
	            bufferedWriter.write(cartChocolate.getShoppingCartId() + ";" +
	            		cartChocolate.getChocolateId() + ";" +
	                    cartChocolate.getAmount() + "\n");
	        }
	        bufferedWriter.flush();
	        bufferedWriter.close();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	private void loadCartChocolates(String contextPath) {
		this.cartChocolates = new ArrayList<CartChocolate>();
		BufferedReader in = null;
		try {
			File file = new File(contextPath + "/cartChocolates.txt");
			System.out.println(file.getCanonicalPath());
			in = new BufferedReader(new FileReader(file));
			String line;
			StringTokenizer st;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.equals("") || line.indexOf('#') == 0)
					continue;
				st = new StringTokenizer(line, ";");
				int shoppingCartid = Integer.parseInt(st.nextToken().trim());
				int chocolateId = Integer.parseInt(st.nextToken().trim());
				int amountId = Integer.parseInt(st.nextToken().trim());
				System.out.println("dosao do ovde crrtitititi");
				cartChocolates.add(new CartChocolate(shoppingCartid, chocolateId, amountId));
				
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
		
		System.out.println(cartChocolates);
	}
	
}
