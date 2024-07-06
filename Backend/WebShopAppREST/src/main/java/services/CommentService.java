package services;

import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import beans.Comment;
import beans.Purchase;
import dao.CommentDAO;
import dao.PurchaseDAO;

@Path("/comments")
public class CommentService {
    
    @Context
    ServletContext ctx;

    @PostConstruct
    public void init() {
        if (ctx.getAttribute("commentDAO") == null) {
            String contextPath = ctx.getRealPath("");
            ctx.setAttribute("commentDAO", new CommentDAO(contextPath));
        }
    }

    @GET
    @Path("/getAll")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Comment> getAllComments() {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        return dao.getAllComments();
    }
    
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addComment(Comment comment) {
        CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        dao.saveComment(comment);
    }
    
    @GET
    @Path("/{managerUsername}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Comment> getPurchasesByManagerId(@PathParam("managerUsername") String username) {
    	System.out.println("DOBAVLJENI USERNAME MANAGERA "+ username);
    	CommentDAO dao = (CommentDAO) ctx.getAttribute("commentDAO");
        ArrayList<Comment> comments = dao.findManagersFactoryComments(username);
        System.out.println("DOBAVLJENI KOMENTARI" + comments);
        return comments;
    }
}
