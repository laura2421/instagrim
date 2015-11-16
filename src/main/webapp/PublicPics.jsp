<%-- 
    Document   : PublicPics
    Created on : 2015-10-14, 13:34:11
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="/Instagrim/Styles.css" />
        <style type="text/css">    
            body{    
                    background-image: url(/Instagrim/img/background.jpg);    
                    background-repeat: repeat;
                }    
        </style>
    </head>
    <body>
        <header>        
            <h1>InstaGrim ! </h1>
            <h2>Your world in Black and White</h2>        
            <a href="#">
                    <span class="header"><a href="/Instagrim">Home</a></span>
            </a>
        </header>
        
        <article>
        
        <%
            Integer reminder = (Integer) request.getAttribute("reminder");
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics != null) {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            int i=0;
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();  
                i++;
        %>
            <div id="PicDetail">
                <div class="title">
                    From:<%=p.getUser()%> At:<%=p.getTime()%>
                </div>
                <div class="pic">
                    <a href="#">
                        <a href="/Instagrim/Image/<%=p.getSUUID()%>" ><img src="/Instagrim/DisplayAll/<%=p.getSUUID()%>">
                    </a>        
                    </a>
                </div>
                <ul>
                    <a href="/Instagrim/AddThumb/<%=p.getSUUID()%>">Like</a> : <%=p.getThumbNum()%>
                    <a href="/Instagrim/viewComment/<%=p.getSUUID()%>">Comment</a> : <%=p.getMessageNum()%>
                </ul>
        <%                       
            java.util.LinkedList<PicComment> lsComments = (java.util.LinkedList<PicComment>) request.getAttribute("Comments");
            if (lsComments != null) {
            Iterator<PicComment> Iterator;
            Iterator = lsComments.iterator();
            while (Iterator.hasNext()) {
                PicComment pc = (PicComment) Iterator.next();
                if(!pc.getPicSUUID().equals(p.getSUUID())){break;}
                else{%><li><%=pc.getCommentator()%>: <%=pc.getPicComment()%></li><%}
                
            }
            }
            
        %>
        
        <%
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            if (lg!=null){
        %>
                <form method="POST" action="PublicImage">
                    <div class="comment" >Say something <input type="text" name="comment" class=text></div>
                    <input type="hidden" name="image" value="<%=p.getSUUID()%>" >
                    <input type="hidden" name="commentator" value="<%=lg.getUsername()%>" >
                    <div class="button"><input type="submit" value="Submit" class="submit"></a></div>
                </form>                                           
        <%
            }else{
        %>
                <div class="notLogin">
                    <a href="/Instagrim/login.jsp">Login</a>/<a href="/Instagrim/register.jsp">Register</a> to leave a message!
                </div>
            
        <%
            }%></div><%
            if(reminder!=null){
                if(i==reminder){break;}
            }
            }
            }

        %>        
            
        </article>
       
    </body>
</html>
