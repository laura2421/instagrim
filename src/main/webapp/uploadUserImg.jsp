<%-- 
    Document   : uploadUserImg
    Created on : 2015-10-12, 21:17:55
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
        <link rel="stylesheet" type="text/css" href="Styles.css" />        
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
            <div id="uploadUser">
                <h3>User Image Upload</h3>
                <form method="POST" enctype="multipart/form-data" action="UserImage">
                    File to upload: <input type="file" name="upfile"><br/><br>
                    <input type="submit" value="Apply"><br/>
                    <%                        
                        LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");                            
                    %>                                       
                </form>
                <h4><a href="/Instagrim/UserImages/<%=lg.getUsername()%>">Choose from your pics</a></h4>
            </div>

        </article>
        
    </body>
</html>
