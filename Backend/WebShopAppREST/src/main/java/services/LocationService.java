package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Factory;
import beans.Location;
import beans.User;
import dao.FactoryDAO;
import dao.LocationDAO;
import dao.UserDAO;

@Path("/locations")
public class LocationService {
	 @Context
	    ServletContext ctx;

	    @PostConstruct
	    public void init() {
	        if (ctx.getAttribute("locationDAO") == null) {
	        	String contextPath = ctx.getRealPath("");
	            ctx.setAttribute("locationDAO", new LocationDAO(contextPath));
	        }
	    }

	    @GET
	    @Path("/")
	    @Produces(MediaType.APPLICATION_JSON)
	    public ArrayList<Location> getAllLocations() {
	        LocationDAO dao = (LocationDAO) ctx.getAttribute("locationDAO");
	        return dao.findAll();
	    }
	    
	    @OPTIONS
	    @Path("/save")
	    @Produces(MediaType.APPLICATION_JSON)
	    public boolean corsNewLocation() {
	        return false;
	    }

	    @POST
	    @Path("/save")
	    @Produces(MediaType.APPLICATION_JSON)
	    @Consumes(MediaType.APPLICATION_JSON)
	    public Location newLocation(Location location) {
	        LocationDAO dao = (LocationDAO) ctx.getAttribute("locationDAO");
	        return dao.save(location);
	    }
	    
	    @GET
	    @Path("/findLocation")
	    @Produces(MediaType.APPLICATION_JSON)
	    public Location findLocation(@QueryParam("id") int id) {
	        LocationDAO dao = (LocationDAO) ctx.getAttribute("locationDAO");
	        return dao.findLocation(id);
	    }
	    
}
