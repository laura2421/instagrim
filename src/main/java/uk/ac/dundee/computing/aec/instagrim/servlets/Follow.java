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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import uk.ac.dundee.computing.aec.instagrim.models.User;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.stores.Recommend;

/**
 *
 * @author Administrator
 */
@WebServlet(urlPatterns = {
    "/AddFollowing/*",
    "/RecommendList/*",
    "/Follow/*",
    "/FollowerList/*",
    "/CheckReminder/*",
    "/UnfollowUser/*",
    "/SearchUser"
})
@MultipartConfig

public class Follow extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Follow() {
        super();
        CommandsMap.put("Follow", 1);
        CommandsMap.put("RecommendList", 2);
        CommandsMap.put("CheckReminder",3);
        CommandsMap.put("FollowerList",4);
        CommandsMap.put("UnfollowUser",5);
        CommandsMap.put("AddFollowing",6);
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
                ViewFollow(args[2],request, response);
                break;
            case 2:
                DisplayRecommendList(args[2], request, response);
                break;
            case 3:
                ViewReminders(args[2],args[3],request,response);
                break;
            case 4:
                DisplayFollowerList(args[2],request,response);
                break;
            case 5:
                UnfollowUser(args[2],args[3],request,response);
                break;
            case 6:
                AddFollow(args[2],args[3],request,response);
                break;
            default:
                error("Bad Operator", response);
        }
    }
    
    private void UnfollowUser(String following, String User,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {                    
        User us=new User();
        us.setCluster(cluster);                
        us.RemoveFollowing(following, User);
        us.RemoveFollower(User, following);
        
        reView(User,request,response);
    }
    
    private void ViewReminders(String following, String User,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Map<String,Integer> followings = new HashMap<>();
        LoggedIn lg= new LoggedIn();
        Integer reminder=0;                
        
        User us=new User();
        us.setCluster(cluster);        
        followings = us.UserFollowing(User);         
        
        HttpSession session=request.getSession();
        lg = (LoggedIn) session.getAttribute("LoggedIn");
        lg.setFollowing(followings);
        reminder = followings.get(following);
        us.CleanReminders(followings,User,following);

        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(following);
        RequestDispatcher rd = request.getRequestDispatcher("/PublicPics.jsp");
        request.setAttribute("Pics", lsPics);
        request.setAttribute("reminder", reminder);
        rd.forward(request, response);
    }
    
    private void ViewFollow(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggedIn lg= new LoggedIn();
        HttpSession session=request.getSession();
        lg = (LoggedIn) session.getAttribute("LoggedIn");
        
        User us=new User();
        us.setCluster(cluster);
        lg.setFollower(us.UserFollower(User));
        lg.setFollowing(us.UserFollowing(User));

        RequestDispatcher rd=request.getRequestDispatcher("/follow.jsp");
        rd.forward(request, response);
    }
    
    private void DisplayRecommendList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RecommendFollowing fm = new RecommendFollowing();
        fm.setCluster(cluster);
        java.util.LinkedList<Recommend> lsRecommend = fm.getRecommendsForUser(User);
        RequestDispatcher rd = request.getRequestDispatcher("/follow.jsp");
        request.setAttribute("Recommends", lsRecommend);
        rd.forward(request, response);
    }
    
    private void AddFollow(String getFriend,String username,HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
        User us=new User();
        us.setCluster(cluster);
        if(!username.equals(getFriend)){
            us.addFollowing(getFriend,username);
            us.addFollower(getFriend,username);
        }
        reView(username,request,response);
    }
    
    private void DisplayFollowerList(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggedIn lg= new LoggedIn();
        HttpSession session=request.getSession();
        lg = (LoggedIn) session.getAttribute("LoggedIn");
        User us=new User();
        us.setCluster(cluster);

        lg.setFollower(us.UserFollower(User));
        RequestDispatcher rd = request.getRequestDispatcher("/follow.jsp");
        rd.forward(request, response);
    }
    
    private void reView(String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LoggedIn lg= new LoggedIn();
        HttpSession session=request.getSession();
        lg = (LoggedIn) session.getAttribute("LoggedIn");
        User us=new User();
        us.setCluster(cluster);
        lg.setFollower(us.UserFollower(User));
        lg.setFollowing(us.UserFollowing(User));

        System.out.println("Session in servlet "+session);
        
        RequestDispatcher rd=request.getRequestDispatcher("/follow.jsp");
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
        String getFriend=request.getParameter("getFriend");
        String username=request.getParameter("username");
        Set<String> answerSet = new HashSet<>();
        User us=new User();
        us.setCluster(cluster);
        answerSet = us.SearchUser(username, getFriend);
        request.setAttribute("nameSet", answerSet);
        reView(username,request,response);        
    }

    private void error(String mess, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = null;
        out = new PrintWriter(response.getOutputStream());
        out.println("<h1>You have a na error in your input</h1>");
        out.println("<h2>" + mess + "</h2>");
        out.close();
    }
}
