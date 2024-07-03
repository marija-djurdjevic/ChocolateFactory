package services;

import java.util.ArrayList;

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
import beans.Factory;
import beans.ShoppingCart;
import dao.CartChocolateDAO;
import dao.FactoryDAO;
import dao.ShoppingCartDAO;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

@Path("/cartChocolates")
public class CartChocolateService {

	private TokenUtils tokenUtils = new TokenUtils();

 	@Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("cartChocolateDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("cartChocolateDAO", new CartChocolateDAO(contextPath));
        }
    }
    
    @GET
    @Path("/getByCartId")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<CartChocolate> getCartChocolatesByCartId(@QueryParam("cartId") int cartId) {
    	CartChocolateDAO dao = (CartChocolateDAO) ctx.getAttribute("cartChocolateDAO");
        ArrayList<CartChocolate> CartChocolates = dao.findCustomersCartChocolates(cartId);
        return CartChocolates;
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
    public Response newCartChocolate(CartChocolate cartChocolate, @Context HttpServletRequest request) {
    	System.out.println("dosao sam do ovde");
        String token = tokenUtils.getToken(request);
        System.out.println("Token: " + token);
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

        CartChocolateDAO dao = (CartChocolateDAO) ctx.getAttribute("cartChocolateDAO");
        CartChocolate savedCartChocolate = dao.save(cartChocolate);

        return Response.ok(savedCartChocolate).build();
    }
}
