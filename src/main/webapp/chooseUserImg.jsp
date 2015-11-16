<%-- 
    Document   : chooseUserImg
    Created on : 2015-10-10, 18:12:24
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
 
        <article>
            <h1>Choose one in Your Pics</h1>
        <%
            java.util.LinkedList<Pic> lsPics = (java.util.LinkedList<Pic>) request.getAttribute("Pics");
            if (lsPics == null) {
        %>
        <p>No Pictures found</p>
        <%
        } else {
            Iterator<Pic> iterator;
            iterator = lsPics.iterator();
        %>
            <table class="chooseUserImg">
        <%
            int i=0;
            while (iterator.hasNext()) {
                Pic p = (Pic) iterator.next();
                if(i%5==0){
        %>
            <tr>
        <%}
        %>
        
            <a href="#">
                <td><a href="/Instagrim/ChangeUserImg/<%=p.getSUUID()%>" ><img src="/Instagrim/Thumb/<%=p.getSUUID()%>">
                </a></td>         
            </a>
        
        <%  if(i%5==4){%></tr><%}
            i++;
            
            }
            }
        %>
        </table>
        </article>
       
    </body>
</html>
