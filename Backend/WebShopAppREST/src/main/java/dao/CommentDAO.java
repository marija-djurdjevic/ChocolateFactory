package dao;

import beans.Comment;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class CommentDAO {
    private ArrayList<Comment> comments = new ArrayList<>();
    private String contextPath;

    public CommentDAO(String contextPath) {
        this.contextPath = contextPath;
        loadComments();
    }

    public ArrayList<Comment> getAllComments() {
        return comments;
    }

    public void saveComment(Comment comment) {
        // Generate new ID based on the last comment ID + 1
        int newId = comments.isEmpty() ? 0 : comments.get(comments.size() - 1).getId() + 1;
        comment.setId(newId);
        comments.add(comment);
        saveCommentsToFile();
    }

    private void saveCommentsToFile() {
        try {
            String filePath = contextPath + "/comments.txt";
            FileWriter writer = new FileWriter(filePath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            Comment lastComment = comments.get(comments.size() - 1);
            bufferedWriter.write(lastComment.getId() + ";" +
                    lastComment.getCustomerId() + ";" +
                    lastComment.getFactoryId() + ";" +
                    lastComment.getComment() + ";" +
                    lastComment.getRating() + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadComments() {
        this.comments = new ArrayList<>();
        BufferedReader in = null;
        try {
            File file = new File(contextPath + "/comments.txt");
            System.out.println("Loading comments from: " + file.getAbsolutePath());
            in = new BufferedReader(new FileReader(file));
            String line;
            StringTokenizer st;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                if (line.equals("") || line.indexOf('#') == 0)
                    continue;
                st = new StringTokenizer(line, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                int customerId = Integer.parseInt(st.nextToken().trim());
                int factoryId = Integer.parseInt(st.nextToken().trim());
                String commentText = st.nextToken().trim();
                int rating = Integer.parseInt(st.nextToken().trim());
                comments.add(new Comment(id, customerId, factoryId, commentText, rating));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
