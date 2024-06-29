package services;

import java.util.ArrayList;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.Chocolate;
import beans.User;
import beans.roles.Manager;
import dao.ChocolateDAO;
import dao.ManagerDAO;
import dao.UserDAO;

@Path("/managers")
public class ManagerService {
	
    @Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("managerDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("managerDAO", new ManagerDAO(contextPath));
        }
    }

    @GET
    @Path("/available")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Manager> getAvailableManagers() {
        ManagerDAO dao = (ManagerDAO) ctx.getAttribute("managerDAO");
        return dao.findAvailableManagers();
    }
    
    @POST
    @Path("/edit")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Manager editManager(Manager manager) {
        ManagerDAO dao = (ManagerDAO) ctx.getAttribute("managerDAO");
        return dao.updateManager(manager);
    }
}
