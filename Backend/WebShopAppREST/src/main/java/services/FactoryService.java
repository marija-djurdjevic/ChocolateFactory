package services;

import java.net.http.HttpHeaders;
import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.Response;

import beans.Chocolate;
import beans.Factory;
import beans.roles.Worker;
import dao.FactoryDAO;
import io.jsonwebtoken.Claims;
import utils.TokenUtils;

@Path("/factories")
public class FactoryService {

    private TokenUtils tokenUtils = new TokenUtils();

    @Context
    ServletContext ctx;
    
    @PostConstruct
    public void init() {
        if (ctx.getAttribute("factoryDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("factoryDAO", new FactoryDAO(contextPath));
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Factory> getAllFactories() {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.findAll();
    }
    
    @GET
    @Path("/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory getFactoryById(@PathParam("factoryId") int factoryId) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Factory factory = dao.findFactory(factoryId);
        System.out.println(factory);
        return factory;
    }
    
    @GET
    @Path("/{factoryId}/chocolates")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Chocolate> getChocolatesByFactoryId(@PathParam("factoryId") int factoryId) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.findFactory(factoryId).getAvailableChocolates();
    }
    
    @OPTIONS
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean corsNewFactory() {
        return false;
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response newFactory(Factory factory, @Context HttpServletRequest request) {
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
        if (role == null || !"Administrator".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }

        // Ako je sve u redu, sačuvajte fabriku
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        Factory savedFactory = dao.save(factory);

        return Response.ok(savedFactory).build();
    }

    /*@GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory deleteFactory(@QueryParam("factoryId") int factoryId) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.deleteFactoryById(factoryId);
    }*/
    @DELETE
    @Path("/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory deleteFactory(@PathParam("factoryId") int factoryId) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.deleteFactoryById(factoryId);
    }


    @GET
    @Path("/filter")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Factory> filterFactories(@QueryParam("status") boolean status) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.filterFactoriesByStatus(status);
    }

    @GET 
    @Path("/findFiltered")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Factory> findFilteredFactories() {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.findFilteredFactories();
    }
    
    @PUT
    @Path("/{factoryId}/updateChocolateAmount/{chocolateId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateChocolateAmount(@PathParam("factoryId") int factoryId, 
                                          @PathParam("chocolateId") int chocolateId, 
                                          @QueryParam("newAmount") int newAmount, 
                                          @Context HttpServletRequest request) {
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
         System.out.println("trenutna uloga jeeee: " + role);
         if (role == null || (!"Worker".equals(role) && !"Customer".equals(role))) {
         	System.out.println("ovo se desilo o o o istototo");
             return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
         }
        
         
        String username = claims.getSubject();
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        if (!dao.isWorkerAtFactory(username, factoryId) && !"Customer".equals(role) && !"Worker".equals(role)) {
        	System.out.println("ovo se desilo o o o");
            return Response.status(Response.Status.FORBIDDEN).entity("You are not the worker in this factory").build();
        }
        System.out.println(newAmount);
        Chocolate updatedChocolate = dao.updateChocolateAmountInFactory(factoryId, chocolateId, newAmount);

        if (updatedChocolate != null) {
            return Response.ok(updatedChocolate).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Factory or chocolate not found").build();
        }
    }

    /*@GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory findFactoryByManagerId(@QueryParam("managerId") int managerId) {
        FactoryDAO factoryDAO = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return factoryDAO.findFactoryByManagerId(managerId);
    }*/
    @GET
    @Path("/find")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory findFactoryByUsername(@QueryParam("username") String username) {
        FactoryDAO factoryDAO = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return factoryDAO.findFactoryByManagerId(username);
    }

    @POST
    @Path("/{factoryId}/addWorker")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addWorkerToFactory(@PathParam("factoryId") int factoryId, Worker worker, @Context HttpServletRequest request) {
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
        if (role == null || !"Manager".equals(role)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You do not have permission to perform this action").build();
        }
        String username = claims.getSubject();
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        if (!dao.isManagerOfFactory(username, factoryId)) {
            return Response.status(Response.Status.FORBIDDEN).entity("You are not the manager of this factory").build();
        }
       
        Worker addedWorker = dao.addWorkerToFactory(factoryId, worker);
        if (addedWorker != null) {
            return Response.ok(addedWorker).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Factory not found").build();
        }
    }
}
