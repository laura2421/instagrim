<%-- 
    Document   : index
    Created on : Sep 28, 2014, 7:01:44 PM
    Author     : Administrator
--%>

<%@page import="java.util.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="uk.ac.dundee.computing.aec.instagrim.stores.*" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <style type="text/css">    
            body{    
                    background-image: url(/Instagrim/img/background.jpg);    
                    background-repeat: repeat;
                }    
        </style>
    </head>
    <body>
                
        <header>
            <h1>InstaGrim !</h1>
            <h2>Your world in Black and White</h2>
            <h3><a href="/Instagrim">Home</a></h3>            
        </header>                                              
            <%                       
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                String UserPic = "user";
                if (lg != null) {
                    if (lg.getlogedin()) {
                        if (lg.getUserImgNum()== null){
                            if(lg.getGender()!=null){UserPic = lg.getGender();}                                                       
            %>
            <div id="indexLeftColumn">
                <div class="picBox">
                    <a href="#">
                        <a href="uploadUserImg.jsp"><img src="${initParam.genderImgPath}<%=UserPic%>Img.png"
                        alt="Gender"></a>
                    </a>                               
                </div>
                <div class="userprofile">
                     Welcome,<%=lg.getUsername()%><br>       
                </div>
            </div>
            <div id="indexRightColumn">
                <div class="choiceList">
                    <li><a href="/Instagrim/PublicImageList">More Images</a><br></li>
                    <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a><br></li>
                    <li><a href="upload.jsp">Upload</a><br></li>
                    <li><a href="/Instagrim/Follow/<%=lg.getUsername()%>">Following/Followers</a></li>
                    <li><a href="/Instagrim/getProfiles/<%=lg.getUsername()%>">User Profiles</a><br></li>
                    <li><a href="/Instagrim/Logoff">Log Off</a></li>
                </div>
            </div>
            <%   
                    }else{
            %>
            <div id="indexLeftColumn">
                <div class="picBox">
                    <a href="#">                            
                        <a href="uploadUserImg.jsp"><img src="/Instagrim/UserThumb/<%=lg.getUsername()%>"
                        alt="UserImg"></a>
                    </a>                               
                </div>
                <div class="userprofile">
                     Welcome,<%=lg.getUsername()%><br>       
                </div>
            </div>
            <div id="indexRightColumn">
                <div class="choiceList">
                    <li><a href="/Instagrim/PublicImageList">More Images</a><br></li>
                    <li><a href="/Instagrim/Images/<%=lg.getUsername()%>">Your Images</a><br></li>
                    <li><a href="upload.jsp">Upload</a><br></li>
                    <li><a href="/Instagrim/Follow/<%=lg.getUsername()%>">Following/Followers</a></li>
                    <li><a href="/Instagrim/getProfiles/<%=lg.getUsername()%>">User Profiles</a><br></li>
                    <li><a href="/Instagrim/Logoff">Log Off</a></li>
                </div> 
            </div>
            <%}          
                    } }else{
            %> 
            <div id="indexLeftColumn">
                <div class="picBox">
                    <a href="#">
                        <a href="login.jsp"><img src="${initParam.genderImgPath}<%=UserPic%>Img.png"
                        alt="Gender"></a>
                    </a>                               
                </div>
                <div class="userprofile">
                     User: unknown<br>       
                </div>               
            </div>
            <div id="indexRightColumn">            
                <div class="choiceList">
                    <li><a href="/Instagrim/PublicImageList">More Images</a><br></li>
                    <li><a href="register.jsp">Register</a><br></li>                              
                    <li><a href="login.jsp">Login</a><br></li>
                </div>
            </div>
            <%                                                                 
                    }%>

        
        <footer>
                &COPY; Andy C
        </footer> 
       
    </body>
</html>
