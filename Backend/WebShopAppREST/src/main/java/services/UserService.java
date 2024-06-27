package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Factory;
import beans.User;
import dao.FactoryDAO;
import dao.UserDAO;

@Path("/users")
public class UserService {

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
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public User newUser(User user) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        return dao.save(user);
    }
    @POST
    @Path("/login")
    @Produces(MediaType.APPLICATION_JSON)
    public User login(@QueryParam("username") String username, @QueryParam("password") String password) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        User authenticatedUser = dao.authenticateUser(username, password);
        return authenticatedUser;
    }
}
