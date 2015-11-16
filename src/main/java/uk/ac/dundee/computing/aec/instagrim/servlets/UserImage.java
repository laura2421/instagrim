package uk.ac.dundee.computing.aec.instagrim.servlets;

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
import javax.servlet.http.Part;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.models.UserPicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

/**
 * Servlet implementation class UserImage
 */
@WebServlet(urlPatterns = {
    "/UserImage",
    "/UserImage/*",
    "/UserThumb/*",
    "/UserImages",
    "/UserImages/*",
    "/ChangeUserImg/*"
})
@MultipartConfig

public class UserImage extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;   
    private HashMap CommandsMap = new HashMap();
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserImage() {
        super();

        CommandsMap.put("UserImage", 1);
        CommandsMap.put("UserThumb", 2);
        CommandsMap.put("UserImages",3);
        CommandsMap.put("ChangeUserImg",4);

    }

    /**
     *
     * @param config
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        cluster = CassandraHosts.getCluster();
    }

    /**
     * @param request    
     * @param response    
     * @throws javax.servlet.ServletException    
     * @throws java.io.IOException    
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
                DisplayUserImage(Convertors.DISPLAY_PROCESSED, args[2], response);
                break;
            case 2:
                DisplayImage(args[2],  response);
                break;
            case 3:
                ChooseImageList(args[2], request, response);
                break;
            case 4:
                ChangeImage(args[2], request, response);
                break;            
            default:
                error("Bad Operator", response);
        }
        
    }

    private void DisplayImage(String user, HttpServletResponse response) throws ServletException, IOException {
        UserPicModel utm = new UserPicModel();
        utm.setCluster(cluster);
  
        
        Pic p = utm.getPic(user);
        
        OutputStream out = response.getOutputStream();

        response.setContentType(p.getType());
        response.setContentLength(p.getLength());

        InputStream is = new ByteArrayInputStream(p.getBytes());
        BufferedInputStream input = new BufferedInputStream(is);
        byte[] buffer = new byte[8192];
        for (int length = 0; (length = input.read(buffer)) > 0;) {
            out.write(buffer, 0, length);
        }
        out.close();
    }
    
    private void DisplayUserImage(int type,String Image, HttpServletResponse response) throws ServletException, IOException {
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
    
    private void ChooseImageList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        RequestDispatcher rd = request.getRequestDispatcher("/chooseUserImg.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }
    
    private void ChangeImage(String Image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        java.util.UUID picid = java.util.UUID.fromString(Image);
        UserPicModel upm = new UserPicModel();
        upm.setCluster(cluster);
        
        Pic p = upm.getUserPic(picid);
        byte[] b=p.getBytes();        

        String type=p.getType();
        String user=p.getUser();
        upm.insertUserPic(b, type, user);
        reIndex(user,request,response);
        response.sendRedirect("/Instagrim");
    }       
    
    private void reIndex(String username, HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {       
        User us=new User();
        us.setCluster(cluster);
        HttpSession session=request.getSession();
        System.out.println("Session in servlet "+session);
        LoggedIn lg= new LoggedIn();
        lg.setLogedin();
        lg.setUsername(username);
        lg.setGender(us.UserGender(username));
        lg.setUserImgNum(us.UserImgNum(username));
            
        session.setAttribute("LoggedIn", lg);
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for (Part part : request.getParts()) {
            System.out.println("Part Name " + part.getName());

            String type = part.getContentType();
            //String filename = part.getSubmittedFileName();
            
            InputStream is = request.getPart(part.getName()).getInputStream();
            int i = is.available();
            HttpSession session=request.getSession();
            LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
            String username="majed";
            if (lg.getlogedin()){
                username=lg.getUsername();
            }
            if (i > 0) {
                byte[] b = new byte[i + 1];
                is.read(b);
                System.out.println("Length : " + b.length);
                UserPicModel tm = new UserPicModel();
                tm.setCluster(cluster);
                tm.insertUserPic(b, type, username);

                is.close();
            }
            reIndex(username,request,response);
            RequestDispatcher rd = request.getRequestDispatcher("/uploadUserImg.jsp");
            rd.forward(request, response);
        }

    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a na error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
    }
}
