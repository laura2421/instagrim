/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
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
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Administrator
 */
@WebServlet(urlPatterns = {
    "/getProfiles/*",
    "/EditProfiles/*",
    "/EditProfiles",
    "/DeleteEmail/*"
})
@MultipartConfig

public class UserProfiles extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserProfiles() {
        super();
        CommandsMap.put("getProfiles", 1);
        CommandsMap.put("EditProfiles", 2);
        CommandsMap.put("DeleteEmail", 3);
    }

    public void init(ServletConfig config) throws ServletException {
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
                getProfiles(args[2],request, response);
                break;
            case 2:
                editProfiles(args[2], request, response);
                break;
            case 3:
                deleteEmail(args[2],request,response);
            default:
                error("Bad Operator", response);
        }
    }

    
    
    
    private void getProfiles(String username, HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {       
        LoggedIn lg= new LoggedIn();
        LoggedIn LG= new LoggedIn();
        lg.setLogedin();
        
        User us=new User();
        us.setCluster(cluster);
        LG = us.getProfiles(username);
        lg.setGender(LG.getGender());
        lg.setFirst_name(LG.getLast_name());
        lg.setLast_name(LG.getLast_name());    
        lg.setUsername(username);
        lg.setEmail(LG.getEmail());

        HttpSession session=request.getSession();
        session.setAttribute("LoggedIn", lg);
        System.out.println("Session in servlet "+session);
        RequestDispatcher rd = request.getRequestDispatcher("/UserProfiles.jsp");
        rd.forward(request, response);
    }
    
    private void editProfiles(String username, HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {       
        LoggedIn lg= new LoggedIn();
        LoggedIn LG= new LoggedIn();
        lg.setLogedin();
        
        User us=new User();
        us.setCluster(cluster);
        LG = us.getProfiles(username);
        lg.setGender(LG.getGender());
        lg.setFirst_name(LG.getLast_name());
        lg.setLast_name(LG.getLast_name());    
        lg.setUsername(username);
        lg.setEmail(LG.getEmail());

        HttpSession session=request.getSession();
        session.setAttribute("LoggedIn", lg);
        System.out.println("Session in servlet "+session);
        RequestDispatcher rd = request.getRequestDispatcher("/editProfiles.jsp");
        rd.forward(request, response);
    }
    
    private void deleteEmail(String email, HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        HttpSession session=request.getSession();
        LoggedIn lg= (LoggedIn)session.getAttribute("LoggedIn");
        LoggedIn LG= new LoggedIn();
        String username=lg.getUsername();
        
        User us=new User();
        us.setCluster(cluster);
        us.RemoveEmail(email, username);
        
        LG = us.getProfiles(username);
        lg.setGender(LG.getGender());
        lg.setFirst_name(LG.getLast_name());
        lg.setLast_name(LG.getLast_name());    
        lg.setEmail(LG.getEmail());

        session.setAttribute("LoggedIn", lg);
        System.out.println("Session in servlet "+session);
        RequestDispatcher rd = request.getRequestDispatcher("/editProfiles.jsp");
        rd.forward(request, response);
    }
    
    private void reProfiles(String username, HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {       
        LoggedIn lg= new LoggedIn();
        LoggedIn LG= new LoggedIn();
        lg.setLogedin();
        
        User us=new User();
        us.setCluster(cluster);
        LG = us.getProfiles(username);
        lg.setGender(LG.getGender());
        lg.setFirst_name(LG.getLast_name());
        lg.setLast_name(LG.getLast_name());    
        lg.setUsername(username);
        lg.setEmail(LG.getEmail());
        lg.setUserImgNum(us.UserImgNum(username));

        HttpSession session=request.getSession();
        session.setAttribute("LoggedIn", lg);
        System.out.println("Session in servlet "+session);
        RequestDispatcher rd = request.getRequestDispatcher("/UserProfiles.jsp");
        rd.forward(request, response);
    }




    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String first_name=request.getParameter("firstname");
        String last_name=request.getParameter("lastname");
        String gender=request.getParameter("gender");
        String email=request.getParameter("email");       
        String password=request.getParameter("password");
        String username=request.getParameter("username");
        User us=new User();
        us.setCluster(cluster);
        us.EditUser(first_name,last_name,gender,email,password,username); 
        reProfiles(username,request,response);        
    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a na error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
    }
}
