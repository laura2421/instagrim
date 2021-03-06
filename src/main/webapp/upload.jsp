<%-- 
    Document   : upload
    Created on : Sep 22, 2014, 6:31:50 PM
    Author     : Administrator
--%>

<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");                                                
            %>
            <div id="uploadRightColumn">
                <div class="choicelist">
                    <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a><br></li>
                    <li><a href="advancedUpload.jsp">Advanced</a></li>
                </div>
            </div>
            <div id="uploadPic">
                <h3>File Upload</h3>
                <form method="POST" enctype="multipart/form-data" action="Image">
                    File to upload: <input type="file" name="upfile"><br><br><br><br>
                    <input type="submit" value="Upload">
                </form>                
            </div>
            
        </article>
    </body>
</html>
