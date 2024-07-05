package services;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.Purchase;
import beans.roles.Customer;
import dao.CustomerDAO;
import dao.PurchaseDAO;
import utils.TokenUtils;
import io.jsonwebtoken.Claims;

@Path("/purchases")
public class PurchaseService {
	
	private TokenUtils tokenUtils = new TokenUtils();
	private PurchaseDAO purchaseDAO;
	private CustomerDAO customerDAO;
	
    @Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("purchaseDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("purchaseDAO", new PurchaseDAO(contextPath));
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Purchase> getAllPurchases() {
        PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
        return dao.findAll();
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response savePurchase(Purchase purchase, @Context HttpServletRequest request) {
        String token = tokenUtils.getToken(request);
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token is missing").build();
        }

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

        String role = claims.get("role", String.class);
        if (role == null || !"Customer".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
        Purchase savedPurchase = dao.save(purchase);
        return Response.ok(savedPurchase).build();
    }
    
    @GET
    @Path("/factory/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchasesByFactory(@PathParam("factoryId") int factoryId, @Context HttpServletRequest request) {
        PurchaseDAO purchaseDAO = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
        /*String token = request.getHeader("Authorization");

        // Proveri validnost tokena i ulogu korisnika (ovo treba implementirati)
        if (token == null) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }*/

        List<Purchase> purchases = purchaseDAO.loadPurchasesWithChocolatesForFactory(factoryId);;
        return Response.ok(purchases).build();
    }

    private boolean isManager(String token) {
        // Implementiraj proveru tokena i uloge korisnika
        return true; // Placeholder
    }
    /*@GET
    @Path("/factory/{factoryId}/customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomersByFactory(@PathParam("factoryId") int factoryId) {
        List<Purchase> purchases = purchaseDAO.findByFactoryId(factoryId);
        Set<Customer> customers = new HashSet<>();
        for (Purchase purchase : purchases) {
            Customer customer = customerDAO.findById(purchase.getCustomerId());
            if (customer != null) {
                customers.add(customer);
            }
        }
        return Response.ok(customers).build();
    }*/
    
    @GET
    @Path("/factory/{factoryId}/customers")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCustomersByFactory(@PathParam("factoryId") int factoryId) {
    	PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
        List<Customer> customers = dao.findCustomersByFactoryId(factoryId);
        return Response.ok(customers).build();
    }
    /*@GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPurchaseById(@PathParam("id") String id) {
        PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
        Purchase purchase = dao.findPurchaseById(id);
        if (purchase == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Purchase not found").build();
        }
        return Response.ok(purchase).build();
    }*/

    /*@PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePurchase(Purchase purchase, @Context HttpServletRequest request) {
        String token = tokenUtils.getToken(request);
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token is missing").build();
        }

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

        String role = claims.get("role", String.class);
        if (role == null || !"Customer".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        PurchaseDAO dao = (PurchaseDAO) ctx.getAttribute("purchaseDAO");
        Purchase updatedPurchase = dao.update(purchase);
        return Response.ok(updatedPurchase).build();
    }*/
}
