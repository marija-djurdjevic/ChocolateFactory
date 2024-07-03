package services;

import java.util.ArrayList;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import beans.User;
import beans.roles.Customer;
import dao.CustomerDAO;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

@Path("/customers")
public class CustomerService {

    private TokenUtils tokenUtils = new TokenUtils();

    @Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("customerDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("customerDAO", new CustomerDAO(contextPath));
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Customer> getAllCustomers() {
        CustomerDAO dao = (CustomerDAO) ctx.getAttribute("customerDAO");
        return dao.findAll();
    }

    @PUT
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateCustomer(User userEdit, @Context HttpServletRequest request) {
        String token = tokenUtils.getToken(request);
        System.out.println("Token: " + token);
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token is missing").build();
        }

        // Verify token
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

        // Check user role
        String role = claims.get("role", String.class);
        if (role == null || !"Customer".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        CustomerDAO dao = (CustomerDAO) ctx.getAttribute("customerDAO");
        Customer updatedCustomer = dao.update(userEdit);

        if (updatedCustomer != null) {
            return Response.ok(updatedCustomer).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Customer not found").build();
        }
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response saveCustomer(User user, @Context HttpServletRequest request) {
        // Extract token from request header
        String token = tokenUtils.getToken(request);
        System.out.println("Token: " + token);
        if (token == null || token.isEmpty()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token is missing").build();
        }

        // Verify token
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

        // Check user role
        String role = claims.get("role", String.class);
        if (role == null || !"Administrator".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        // Save customer
        CustomerDAO dao = (CustomerDAO) ctx.getAttribute("customerDAO");
        Customer savedCustomer = dao.save(user, ctx.getRealPath(""));

        return Response.ok(savedCustomer).build();
    }
}

