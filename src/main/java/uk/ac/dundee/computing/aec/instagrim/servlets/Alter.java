package uk.ac.dundee.computing.aec.instagrim.servlets;

import com.datastax.driver.core.Cluster;
import com.jhlabs.image.BlockFilter;
import com.jhlabs.image.DitherFilter;
import com.jhlabs.image.EmbossFilter;
import com.jhlabs.image.FeedbackFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.jhlabs.image.MarbleFilter;
import com.jhlabs.image.StampFilter;
import com.jhlabs.image.ThresholdFilter;
import com.jhlabs.image.WaterFilter;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uk.ac.dundee.computing.aec.instagrim.lib.CassandraHosts;
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;
import uk.ac.dundee.computing.aec.instagrim.models.Comment;
import uk.ac.dundee.computing.aec.instagrim.models.PicModel;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;
import uk.ac.dundee.computing.aec.instagrim.models.PicAlter;

/**
 * Servlet implementation class Image
 */
@WebServlet(urlPatterns = {
    "/Alter/*",
    "/Alter",
    "/DownloadPic/*",
    "/DeletePic/*",
    "/DeleteComment/*"
})
@MultipartConfig

public class Alter extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private Cluster cluster;
    private HashMap CommandsMap = new HashMap();
    
    

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Alter() {
        super();
        CommandsMap.put("Alter", 1);
        CommandsMap.put("DownloadPic",2);
        CommandsMap.put("DeletePic", 3);
        CommandsMap.put("DeleteComment", 4);
    }

    @Override
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
                DisplayAlterList(args[2], request, response);
                break;
            case 2:
                DownloadPic(args[2], response);
                break;
            case 3:
                DeletePic(args[2], args[3], request, response);
                break;
            case 4:
                DeleteComment(args[2],args[3],args[4],request,response);
                break;
            default:
                error("Bad Operator", response);
        }
    }
    
    private void DeletePic(String Image, String User, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        tm.RemovePic(java.util.UUID.fromString(Image), User);
        /*
        Comment cm = new Comment();
        cm.setCluster(cluster);
        cm.removeAll(java.util.UUID.fromString(Image));*/
        
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }
    
    private void DeleteComment(String image,String comment,String user,HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{
        Comment cm = new Comment();
        cm.setCluster(cluster);
        cm.removeComment(java.util.UUID.fromString(image), comment);  
        cm.reduceCommentNum(java.util.UUID.fromString(image));
        
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(user);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }
    
    private void DownloadPic(String Image,HttpServletResponse response) throws ServletException, IOException {
        PicModel tm = new PicModel();
        tm.setCluster(cluster);        
        Pic p = tm.getPic(Convertors.DISPLAY_PROCESSED,java.util.UUID.fromString(Image));        
       
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType("jpg");
            response.setContentLength(p.getLength());
            
            InputStream is = new ByteArrayInputStream(p.getBytes());
            BufferedInputStream input = new BufferedInputStream(is);
            byte[] buffer = new byte[8192];
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                out.write(buffer, 0, length);
            }
        }
    }
        

    private void DisplayAlterList(String Image, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Pic p = new Pic();
        p.setUUID(java.util.UUID.fromString(Image));
        
        RequestDispatcher rd = request.getRequestDispatcher("/picFilter.jsp");
        request.setAttribute("Pic", p);
        rd.forward(request, response);

    }
    
    private void Preview(int type,java.util.UUID picid,BufferedImage BI,HttpServletResponse response) throws ServletException, IOException {
        byte[] b;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(BI, "jpg", baos);
            baos.flush();
            b = baos.toByteArray();
        }
        
        PicModel tm = new PicModel();
        tm.setCluster(cluster);         
        Pic p = tm.getPic(type,picid);
        
        try (OutputStream out = response.getOutputStream()) {
            response.setContentType(p.getType());
            response.setContentLength(b.length);
            
            InputStream is = new ByteArrayInputStream(b);
            BufferedInputStream input = new BufferedInputStream(is);
            byte[] buffer = new byte[8192];
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                out.write(buffer, 0, length);
            }
        }
    }
    
    private void Apply(java.util.UUID picid,BufferedImage BI,String user,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try{                  
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(BI, "jpg", baos);
                baos.flush();
            
                byte[] b = baos.toByteArray();
                baos.close();
 
                PicAlter pa = new PicAlter();
                pa.setCluster(cluster);
                pa.getPicAlter(b,picid);        
            }
        catch (Exception e) {  
            e.printStackTrace();  
        }  
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(user);
        RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
        request.setAttribute("Pics", lsPics);
        rd.forward(request, response);
    }
    
    public BufferedImage addFilter(java.util.UUID picid,String Stamp,String Dither,String Emboss,
            String Threshold,String Block,String Marble,String Water,String Feedback) throws IOException{
        try{
            PicModel tm = new PicModel();
            tm.setCluster(cluster);        
            Pic p = tm.getPic(Convertors.DISPLAY_PROCESSED,picid);
            byte[] b=p.getBytes();
            ByteArrayInputStream in = new ByteArrayInputStream(b);
            BufferedImage BI = ImageIO.read(in);     

            BufferedImage tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            BufferedImage bImg = BI;
            if(Stamp!=null){
                bImg = new StampFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Dither!=null){
                bImg = new DitherFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Emboss!=null){
                bImg = new EmbossFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Threshold!=null){
                bImg = new ThresholdFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Block!=null){
                bImg = new BlockFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Marble!=null){
                bImg = new MarbleFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Water!=null){
                bImg = new WaterFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            if(Feedback!=null){
                bImg = new FeedbackFilter().filter(BI, tImg);
                BI = bImg;
                tImg = new BufferedImage(BI.getWidth(), BI.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            }
            bImg = new GrayscaleFilter().filter(BI,tImg);
            BI = bImg;
            return BI;
        }catch (IOException et) {
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String Stamp=request.getParameter("stamp"); 
        String Dither=request.getParameter("dither");
        String Emboss=request.getParameter("emboss");
        String Threshold=request.getParameter("threshold");
        String Block=request.getParameter("block");
        String Marble=request.getParameter("marble");
        String Water=request.getParameter("water");
        String Feedback=request.getParameter("feedback");
        
        String Choice=request.getParameter("choice");
        String Image=request.getParameter("image");
        String User=request.getParameter("user");
        
        java.util.UUID picid = java.util.UUID.fromString(Image);
        
        BufferedImage BI=addFilter(picid,Stamp,Dither,Emboss,Threshold,Block,Marble,Water,Feedback);
        
        if(Choice==null){
            Pic p = new Pic();
            p.setUUID(picid);
            RequestDispatcher rd = request.getRequestDispatcher("/picFilter.jsp");
            request.setAttribute("Pic", p);
            rd.forward(request, response);
        }
        else if(("preview").equals(Choice)){
            Preview(Convertors.DISPLAY_PROCESSED,picid,BI,response);
        }
        else if(("apply").equals(Choice)){
            Apply(picid,BI,User,request,response);
        }
        else if(("cancel").equals(Choice)){
            PicModel tm = new PicModel();
            tm.setCluster(cluster);
            java.util.LinkedList<Pic> lsPics = tm.getPicsForUser(User);
            RequestDispatcher rd = request.getRequestDispatcher("/UsersPics.jsp");
            request.setAttribute("Pics", lsPics);
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
