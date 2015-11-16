<%-- 
    Document   : login.jsp
    Created on : Sep 28, 2014, 12:04:14 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
            <div id="loginColumn">
                <h2>Login</h2>
                <form method="POST"  action="Login">
                    <ul>
                        <h4>User Name <input type="text" name="username"></h4>
                        <h4>Password <input type="password" name="password"></h4>
                    </ul>
                    <br/>
                    <div class="login"><input type="submit" value="Login"></div>
                </form>
            </div>
        </article>

            
    </body>
</html>
