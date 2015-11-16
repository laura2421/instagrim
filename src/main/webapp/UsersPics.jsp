<%-- 
    Document   : UsersPics
    Created on : Sep 24, 2014, 2:52:48 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
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
        
        <nav>
            <ul>
                <li class="nav"><a href="/Instagrim/upload.jsp">Upload</a></li>
            </ul>
        </nav>
 
        <article>
            <h1>Your Pics</h1>
        <%
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
            int i=0;
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();
                i++;
        %>
        
        <div id="display">
            <div class="title">
                From:<%=p.getUser()%> At:<%=p.getTime()%>
            </div>
            <div class="pic">
                <a href="/Instagrim/Image/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>"></a>
            </div>
            <div class="otherAction">
                <div class="filter"><a href="/Instagrim/Alter/<%=p.getSUUID()%>">Filter</a></div>
                <div class="download"><a href="/Instagrim/DownloadPic/<%=p.getSUUID()%>">Download</a></div>
                <div class="delete"><a href="/Instagrim/DeletePic/<%=p.getSUUID()%>/<%=lg.getUsername()%>">Delete</a></div>
            </div>
            <ul>
                <a href="/Instagrim/SelfThumb/<%=p.getSUUID()%>">Like</a> : <%=p.getThumbNum()%>
                <a href="/Instagrim/SelfView/<%=p.getSUUID()%>">Comment</a> : <%=p.getMessageNum()%>
            </ul>
            <%                       
            java.util.LinkedList<PicComment> lsComments = (java.util.LinkedList<PicComment>) request.getAttribute("Comments");
            if (lsComments != null) {
            Iterator<PicComment> Iterator;
            Iterator = lsComments.iterator();
            while (Iterator.hasNext()) {
                PicComment pc = (PicComment) Iterator.next();
                if(!pc.getPicSUUID().equals(p.getSUUID())){break;}
                else{%>
                <li>
                    <%=pc.getCommentator()%>: <%=pc.getPicComment()%>
            <%
                    if(pc.getCommentator().equals(lg.getUsername())){
                        %><a href="/Instagrim/DeleteComment/<%=p.getSUUID()%>/<%=pc.getPicComment()%>/<%=lg.getUsername()%>">Delete</a><%
                    }
            %>
                </li>
                <%}
                
            }
            }
            
        %>
        
        <%
            if (lg!=null){
        %>
                <form method="POST" action="/Instagrim/SelfComment">
                    <div class="comment" >Say something <input type="text" name="comment" class=text></div>
                    <input type="hidden" name="simage" value="<%=p.getSUUID()%>" >
                    <input type="hidden" name="user" value="<%=lg.getUsername()%>" >
                    <div class="button"><input type="submit" value="Submit" class="submit"></a></div>
                </form>                                 
        
        <%
            }
        %>
        </div>
        <%
            }   
            }
        %>
        </article>
       
    </body>
</html>
