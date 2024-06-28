package services;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.User;
import beans.enums.Role;
import dao.UserDAO;

@Path("/users")
public class UserService {

	private String generateToken() {
        return UUID.randomUUID().toString();
    }
	
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
    @Path("/logging")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password) {
        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        User authenticatedUser = dao.authenticateUser(username, password);

        if (authenticatedUser != null) {
            String token = generateToken();

            // Kreiranje kolačića sa tokenom
            NewCookie cookie = new NewCookie("token", token, "/", null, null, 7 * 24 * 60 * 60, false);

            // Dodavanje tokena u zaglavlje odgovora
            return Response.ok(authenticatedUser)
                    .cookie(cookie)
                    .header("X-Token", token)
                    .build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }
    
    @GET
    @Path("/admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkAdminStatus(@Context HttpServletRequest request) {
        String token = getTokenFromCookies(request);
        System.out.println("Token iz kolačića: " + token);

        if (token == null) {
            System.out.println("Token nije pronađen");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Token nije pronađen").build();
        }

        UserDAO dao = (UserDAO) ctx.getAttribute("userDAO");
        User user = dao.findUserByToken(token);
        System.out.println("Korisnik iz tokena: " + user);

        if (user == null) {
            System.out.println("Korisnik nije pronađen");
            return Response.status(Response.Status.UNAUTHORIZED).entity("Korisnik nije pronađen").build();
        }

        boolean isAdmin = user.getRole() == Role.Administrator;
        System.out.println("Da li je korisnik administrator: " + isAdmin);
        return Response.ok(isAdmin).build();
    }

    private String getTokenFromCookies(HttpServletRequest request) {
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
       
}
