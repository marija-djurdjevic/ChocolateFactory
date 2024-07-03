package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.CartChocolate;
import beans.Chocolate;
import beans.Factory;
import beans.ShoppingCart;
import beans.User;
import dao.CartChocolateDAO;
import dao.ChocolateDAO;
import dao.FactoryDAO;
import dao.ShoppingCartDAO;
import dao.UserDAO;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;


@Path("/shoppingCarts")
public class ShoppingCartService {

    private TokenUtils tokenUtils = new TokenUtils();
    CartChocolateDAO cartChocolateDAO;
 	@Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("shoppingCartDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("shoppingCartDAO", new ShoppingCartDAO(contextPath));
            cartChocolateDAO = new CartChocolateDAO(contextPath);
        }
    }
    
    @GET
    @Path("/getByUserId")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart getCartByCustomerId(@QueryParam("userId") int userId) {
    	System.out.println("dosao do usera " + userId);
    	ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        ShoppingCart shoppingCart = dao.findShoppingCart(userId);
        System.out.println(shoppingCart);
        return shoppingCart;
    }
    
    @GET
    @Path("/getCartDetailsByUserId")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getCartDetailsByUserId(@QueryParam("userId") int userId) {
        Map<String, Object> response = new HashMap<>();
        
        // Dobavljanje korpe za datog korisnika
        ShoppingCartDAO cartDao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        ShoppingCart shoppingCart = cartDao.findShoppingCart(userId);
        
        // Ako korpa nije pronađena, možete vratiti odgovarajući status ili obraditi grešku
        if (shoppingCart == null) {
            // Na primer, ovde možete dodati logiku za rukovanje greškom ako korpa nije pronađena
            return null;
        }
        
        // Dobavljanje čokolada u korpi
        ArrayList<CartChocolate> cartChocolates = cartDao.compatibileCartChocolates(shoppingCart);
        
        // Ako nema čokolada u korpi, možete rukovati ovom situacijom prema vašim zahtevima
        if (cartChocolates == null || cartChocolates.isEmpty()) {
            // Na primer, ovde možete dodati logiku za rukovanje situacijom kada nema čokolada u korpi
            return null;
        }
        
        // Dodavanje shoppingCart i cartChocolates u odgovor
        response.put("shoppingCart", shoppingCart);
        response.put("cartChocolates", cartChocolates);
        
        return response;
    }
    
    @GET
    @Path("/{shoppingCartId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart getCartById(@PathParam("shoppingCartId") int shoppingCartId) {
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        return dao.findShoppingCart(shoppingCartId);
    }
    
    @POST
    @Path("/edit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editShoppingCart(ShoppingCart shoppingCart, @Context HttpServletRequest request) {
    	System.out.println("origin Sc " + shoppingCart);
    	String token = tokenUtils.getToken(request);
        System.out.println("Token: " + token);
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token is missing").build();
        }

        // Verifikujte token
        Claims claims;
        try {
            claims = tokenUtils.parseToken(token);
            if (claims == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token parsing error").build();
        }

        // Proverite ulogu korisnika
        String role = claims.get("role", String.class);
        if (role == null || !"Customer".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        // Ako je sve u redu, sačuvajte fabriku
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        ShoppingCart savedSc = dao.updateShoppingCart(shoppingCart);

        System.out.println("savedSc  " + savedSc);
        return Response.ok(savedSc).build();
    }
    
    @OPTIONS
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean corsNewShoppingCart() {
        return false;
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newShoppingCart(ShoppingCart shoppingCart, @Context HttpServletRequest request) {
        // Izvucite token iz zaglavlja zahteva
        String token = tokenUtils.getToken(request);
        System.out.println("Token: " + token);
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token is missing").build();
        }

        // Verifikujte token
        Claims claims;
        try {
            claims = tokenUtils.parseToken(token);
            if (claims == null) {
                return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid token").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token parsing error").build();
        }

        // Proverite ulogu korisnika
        String role = claims.get("role", String.class);
        if (role == null || !"Customer".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        // Ako je sve u redu, sačuvajte fabriku
        ShoppingCartDAO dao = (ShoppingCartDAO) ctx.getAttribute("shoppingCartDAO");
        ShoppingCart savedShoppingCart = dao.save(shoppingCart);

        return Response.ok(savedShoppingCart).build();
    }
}
