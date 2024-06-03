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
import dao.FactoryDAO;

@Path("/factories")
public class FactoryService {

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
    public Factory newFactory(Factory factory) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.save(factory);
    }

    /*@POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public ArrayList<Factory> updateFactory(Factory factory) {
        FactoryDAO dao = (FactoryDAO) ctx.getAttribute("factoryDAO");
        return dao.updateFactory(factory.getId(), factory);
    }*/

    @GET
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Factory deleteFactory(@QueryParam("factoryId") int factoryId) {
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
}
