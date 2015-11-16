<%-- 
    Document   : editProfiles
    Created on : 2015-10-22, 23:21:43
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
            <form id="editForm" method="POST"  action="EditProfiles">
            <div id="userProfiles">
                <%
                    LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
                %>
                <li>First Name: <%if(lg.getFirst_name()!=null){%><%=lg.getFirst_name()%><%}else{%>Unknown<%}%></li>
                Edit: <input type="text" name="firstname" class="type"><br>
                <li>Last Name: <%if(lg.getLast_name()!=null){%><%=lg.getLast_name()%><%}else{%>Unknown<%}%></li>
                Edit: <input type="text" name="lastname" class="type"><br>
                <li>Gender: <%if(lg.getGender()!=null){%><%=lg.getGender()%><%}else{%>Unknown<%}%></li>
                Edit: Male<input  type="radio" id="gender_male" value="male" name="gender" class="type">
                            Female<input  type="radio" id="gender_female" value="female" name="gender"><br>
                <li>Email:
                <%{
                    if(lg.getEmail().isEmpty()){%>Unknown<%}
                    else{
                        for (String str : lg.getEmail()){
                            %><br><%=str%> <a href="/Instagrim/DeleteEmail/<%=str%>">Delete</a><%
                        } 
                    }
                }
                %>
                </li>
                Add Email: <input type="text" name="email" class="type"><br>
                <li>Change Password: <input id="password" type="password" name="password"class="type"></li><br>
                <input type="hidden" name="username" value="<%=lg.getUsername()%>" >
                <input type="submit" value="Save">
            </div>
            </form>
        </article>
    </body>
</html>

