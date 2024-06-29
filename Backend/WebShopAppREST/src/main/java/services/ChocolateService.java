package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
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

import beans.Chocolate;
import beans.Factory;
import dao.ChocolateDAO;
import dao.FactoryDAO;

@Path("/chocolates")
public class ChocolateService {

	@Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("chocolateDAO") == null) {
        	String contextPath = ctx.getRealPath("");
            ctx.setAttribute("chocolateDAO", new ChocolateDAO(contextPath));
        }
    }
    
    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Chocolate> getAllChocolates() {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.findAll();
    }
    
    @GET
    @Path("/{factoryId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Chocolate> getChocolatesByFactoryId(@PathParam("factoryId") int factoryId) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.findChocolatesByFactoryId(factoryId);
    }
    
    @OPTIONS
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean corsNewChocolate() {
        return false;
    }

    @POST
    @Path("/save")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Chocolate newChocolate(Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        String contextPath = ctx.getRealPath("");
        return dao.save(chocolate, contextPath);
    }
    
    @POST
    @Path("/edit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Chocolate editChocolate(Chocolate chocolate) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.updateChocolate(chocolate);
    }
    
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Chocolate deleteChocolate(@PathParam("id") int id) {
        ChocolateDAO dao = (ChocolateDAO) ctx.getAttribute("chocolateDAO");
        return dao.deleteChocolateById(id);
    }
}