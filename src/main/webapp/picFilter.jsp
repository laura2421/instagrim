<%-- 
    Document   : picFilter
    Created on : 2015-10-19, 21:43:11
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
        <div id="alterPic">
            <h1>Choose filter for pic</h1>       
            <%
            LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            Pic p = (Pic) request.getAttribute("Pic");            
            %>     
            <div class="alterImg">
                <img src="/Instagrim/Image/<%=p.getSUUID()%>"></a>
            </div>
            <form id="filterList" method="POST"  action="Alter">
                <div class="filterCategory">                    
                    StampFilter<input id="filter" type="checkbox" name="stamp" value="stamp" class="type">
                    DitherFilter<input id="filter" type="checkbox" name="dither" value="dither" class="type">
                    EmbossFilter<input id="filter" type="checkbox" name="emboss" value="emboss" class="type">
                    ThresholdFilter<input id="filter" type="checkbox" name="threshold" value="threshold" class="type"><br>                   
                    BlockFilter<input id="filter" type="checkbox" name="block" value="block" class="type">
                    MarbleFilter<input id="filter" type="checkbox" name="marble" value="marble" class="type">
                    WaterFilter<input id="filter" type="checkbox" name="water" value="water" class="type">
                    FeedbackFilter<input id="filter" type="checkbox" name="feedback" value="feedback" class="type">
                </div> 
                <div class="choiceCategory">
                    Preview<input id="choose" type="radio" name="choice" value="preview">
                    Apply<input id="choose" type="radio" name="choice" value="apply">
                    Cancel<input id="choose" type="radio" name="choice" value="cancel">
                    <input type="hidden" name="image" value="<%=p.getSUUID()%>">
                    <input type="hidden" name="user" value="<%=lg.getUsername()%>">
                </div>    
                <div class="button"><input type="submit" value="OK"></div>
                
            </form>
        </div>
        

        
            
    </body>
</html>
