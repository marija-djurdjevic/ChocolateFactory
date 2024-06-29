package services;

import java.util.ArrayList;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import beans.roles.Worker;
import dao.WorkerDAO;

@Path("/workers")
public class WorkerService {
	
    @Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("workerDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("workerDAO", new WorkerDAO(contextPath));
        }
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Worker> getAllWorkers() {
        WorkerDAO dao = (WorkerDAO) ctx.getAttribute("workerDAO");
        return dao.findAll();
    }
}
