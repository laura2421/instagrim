<%-- 
    Document   : advancedUpload
    Created on : 2015-10-28, 16:10:24
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
                    <li><a href="upload.jsp">General</a></li>
                </div>
            </div>
            <div id="uploadPic">
                <h3>File Upload</h3>
                <form method="POST" enctype="multipart/form-data" action="AdvancedImage">
                    File to upload: <input type="file" name="upfile"><br>
                    <div class="message"><li>Say something? <input type="text" name="message" class="type"></li></div>
                    <div class="time">
                        <li>Set time to send?                   
                            HH:<input type="text" name="hour" size="1" maxlength="2" class="type">
                            mm:<input type="text" name="minute" size="1" maxlength="2" class="type">
                            ss:<input type="text" name="second" size="1" maxlength="2" class="type">
                        </li>
                    </div>
                    <input type="submit" value="Upload">
                </form>
            </div>
        </article>
            
    </body>
</html>

