<%-- 
    Document   : UserProfiles
    Created on : 2015-10-20, 20:33:15
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
            <div id="userProfiles">
                <%
                    LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                %>
                <li>User Name: <%=lg.getUsername()%></li><br>
                <li>First Name: <%if(lg.getFirst_name()!=null){%><%=lg.getFirst_name()%><%}else{%>Unknown<%}%></li><br>
                <li>Last Name: <%if(lg.getLast_name()!=null){%><%=lg.getLast_name()%><%}else{%>Unknown<%}%></li><br>
                <li>Gender: <%if(lg.getGender()!=null){%><%=lg.getGender()%><%}else{%>Unknown<%}%></li><br>
                <li>Email:
                <%{
                    if(lg.getEmail().isEmpty()){%>Unknown<br><%}
                    else{
                        for (String str : lg.getEmail()){
                            %><%=str%><br><%
                        } 
                    }
                }
                %>
                </li>
                <br>
                <div id="editUP">
                    <a href="/Instagrim/EditProfiles/<%=lg.getUsername()%>">Edit Profiles</a>
                </div>
            </div>
            
        </article>
    </body>
</html>
