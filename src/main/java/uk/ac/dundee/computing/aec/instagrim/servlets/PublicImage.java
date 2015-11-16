package uk.ac.dundee.computing.aec.instagrim.servlets;

import uk.ac.dundee.computing.aec.instagrim.models.Comment;
import com.datastax.driver.core.Cluster;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.RecommendFollowing;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.PicComment;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/DisplayAll/*",
    "/PublicImages",
    "/PublicImageList",
    "/viewComment/*",
    "/AddThumb/*",
    "/SelfThumb/*",
    "/SelfView/*",
    "/PublicImage"
})
@MultipartConfig

public class PublicImage extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public PublicImage() {
        super();
        CommandsMap.put("AddThumb", 1);
        CommandsMap.put("PublicImageList", 2);
        CommandsMap.put("DisplayAll", 3);
        CommandsMap.put("viewComment",4);
        CommandsMap.put("SelfThumb", 5);
        CommandsMap.put("SelfView", 6);
    }

    public void init(ServletConfig config) throws ServletException {
        // TODO Auto-generated method stub
        cluster = CassandraHosts.getCluster();
    }

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String args[] = Convertors.SplitRequestPath(request);
        int command;
        try {
            command = (Integer) CommandsMap.get(args[1]);
        } catch (Exception et) {
            error("Bad Operator", response);
            return;
        }
        switch (command) {
            case 1:
                OtherThumb(args[2],request,response);
                break;
            case 2:
                DisplayImageList(request, response);
                break;
            case 3:
                DisplayImage(Convertors.DISPLAY_THUMB,args[2],  response);
                break;
            case 4:
                DisplayCommentList(args[2],request,response);
                break;
            case 5:
                SelfThumb(args[2],request,response);
                break;
            case 6:
                SelfCommentList(args[2],request,response);
                break;
            default:
                error("Bad Operator", response);
        }
    }
    
    private void OtherThumb(String Image,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        java.util.UUID picid = java.util.UUID.fromString(Image);        
        PicModel tm = new PicModel();       
        tm.setCluster(cluster);
        tm.insertThumb(picid);
        
        java.util.LinkedList<Pic> lsPics = tm.getAllPics();
        AddReference(Image,"You gave a thumb-up to his pic.",request);
        RequestDispatcher rd = request.getRequestDispatcher("/PublicPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }
    
    private void SelfThumb(String Image,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        java.util.UUID picid = java.util.UUID.fromString(Image);
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        tm.insertThumb(picid);
        
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        String username="majed";
        if (lg.getlogedin()){
            username=lg.getUsername();
        }        
        
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(username);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }

    private void AddReference(String Image,String reason,HttpServletRequest request) throws ServletException, IOException{                 
        java.util.UUID picid = java.util.UUID.fromString(Image);
        HttpSession Session=request.getSession();
        LoggedIn lg= (LoggedIn)Session.getAttribute("LoggedIn");
        String username="majed";
        if (lg!=null){
            username=lg.getUsername();
            RecommendFollowing rfm = new RecommendFollowing();
            rfm.setCluster(cluster);
            rfm.InsertReference(username,picid,reason);
        }                
        
    }

    private void DisplayImageList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getAllPics();
        RequestDispatcher rd = request.getRequestDispatcher("/PublicPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);

    }
    
    
    private void DisplayImage(int type,String Image, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
  
        
        Pic p = tm.getPic(type,java.util.UUID.fromString(Image));
        
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());
        //out.write(Image);
        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
    
    private void DisplayCommentList(String Image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Comment cm = new Comment();
        cm.setCluster(cluster);
        java.util.LinkedList<PicComment> lsComments = cm.getCommentsForPic(java.util.UUID.fromString(Image));                
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getAllPics();        
        RequestDispatcher rd = request.getRequestDispatcher("/PublicPics.jsp");
        request.setAttribute("Comments", lsComments);
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);

    }   
    
    private void SelfCommentList(String Image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Comment cm = new Comment();
        cm.setCluster(cluster);
        java.util.LinkedList<PicComment> lsComments = cm.getCommentsForPic(java.util.UUID.fromString(Image));
        
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        String username="majed";
        if (lg.getlogedin()){
            username=lg.getUsername();
        }
        
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(username);        
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Comments", lsComments);
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);

    }   

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param Image
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String comment=request.getParameter("comment");    
        String image=request.getParameter("image");
        String commentator=request.getParameter("commentator");
        java.util.UUID picid = java.util.UUID.fromString(image);
        Comment cm = new Comment();
        cm.setCluster(cluster);
        cm.insertComment(comment, commentator, picid);                
        cm.addCommentNum(picid);
        AddReference(image,"You left a comment on his pic.",request);
        
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getAllPics();        
        RequestDispatcher rd = request.getRequestDispatcher("/PublicPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }
        
        private void error(String mess, HttpServletResponse response) throws ServletException, IOException {
            PrintWriter out = null;
            out = new PrintWriter(response.getOutputStream());
            out.println("<h1>You have a na error in your input</h1>");
            out.println("<h2>" + mess + "</h2>");
            out.close();
        }
        
    }

    

