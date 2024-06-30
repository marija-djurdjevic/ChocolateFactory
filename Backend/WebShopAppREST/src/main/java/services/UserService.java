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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import beans.User;
import beans.enums.Role;
import beans.roles.Customer;
import beans.roles.Manager;
import dao.UserDAO;
import dto.LoginResponseDTO;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

@Path("/users")
public class UserService {
	
	private TokenUtils tokenUtils = new TokenUtils();
	
    @Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("userDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("userDAO", new UserDAO(contextPath));
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<User> getAllUsers() {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        return dao.findAll();
    }

    @OPTIONS
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean corsNewUser() {
        return false;
    }

    @POST
    @Path("/saveCustomer")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newUser(User user, @Context HttpServletRequest request) {
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
        if (role != null) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }
        
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        User usercustomer = dao.saveCustomer(user);
        return Response.ok(usercustomer).build();
    }
    
    @POST
    @Path("/saveManager")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newManager(User user, @Context HttpServletRequest request) {
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
        if (role == null || !"Administrator".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        Manager manager = dao.saveManager(user);
        
        return Response.ok(manager).build();
    }

    @POST
    @Path("/logging")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        User authenticatedUser = dao.authenticateUser(username, password);

        if (authenticatedUser != null) {
        	String role = authenticatedUser.getRole().toString();
            String token = tokenUtils.generateToken(username, role);
            
            LoginResponseDTO response = new LoginResponseDTO(username, role, token);

            return Response.ok(response)
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }
       
}
