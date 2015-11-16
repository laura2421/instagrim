<%-- 
    Document   : register.jsp
    Created on : Sep 28, 2014, 6:29:51 PM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Instagrim</title>
        <link rel="stylesheet" type="text/css" href="Styles.css" />
        <script type="text/javascript" src="js/jquery-1.4.2.js"></script>
        <script type="text/javascript" src="js/jquery.metadata.js"></script>
        <script src="js/jquery.validate.js" type="text/javascript"></script>
        <style type="text/css">    
            body{    
                    background-image: url(/Instagrim/img/background.jpg);    
                    background-repeat: repeat;
                }    
        </style>
        
        <script type="text/javascript">
            $(document).ready(function(){
                $("#registerForm").validate();
                $("#registerForm").validate({
                    debug:true
                });   

            });                               
        </script>

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
                String checkname = (String)request.getAttribute("checkname");
            %>            
            <form id="registerForm" method="POST"  action="Register">  
                
                <div id="registerColumn">
                    <h2>Register</h2>            
                    <h4><div class="registerName">User Name <input type="text" name="username" class="required type">
                        </div>
                    </h4>                    
                    <h4><div class="registerPass">Password <input id="password" type="password" name="password"class="required type"></div></h4>
                    <h4><div class="registerComfirm">Comfirm Password <input type="password" name="comfirmPassword"class="{required:true,equalTo:'#password'}">                            
                        </div>
                    </h4>
                    <h4><div class="registerEmail">Email <input type="email" name="email" class="type">                            
                        </div>
                    </h4>
                    <h4><div class="registerGender">
                            Male<input  type="radio" id="gender_male" value="male" name="gender" class="type">
                            Female<input  type="radio" id="gender_female" value="female" name="gender">
                        </div>
                    </h4>
            
                    <div class="enter"><input type="submit" value="Register">
            <%
                if ("error".equals(checkname)){
                    %><h5>This name is registered!</h5><%
                }
            %>
                    </div>
                </div>
                
            </form>            
            
        </article>         
        
    </body>
</html>