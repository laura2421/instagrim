<%-- 
    Document   : follow
    Created on : 2015-10-24, 11:06:22
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
            <%
                LoggedIn lg = (LoggedIn) session.getAttribute("LoggedIn");
            %>
            <div id="follow">
                <div class="followerlist">
                    My Followers:
                <%                    
                    if(lg.getFollower().isEmpty()){%>No Follower<br><%}
                    else{
                        for(String str:lg.getFollower()){
                            %><li><a href="/Instagrim/Images/<%=str%>"><%=str%></a></li><%
                        }                           
                    }
                %>
                </div>
                <div class="followinglist">
                    My Following:
                <%
                    if(lg.getFollowing().isEmpty()){%>No Following<br><%}
                    else{
                        Set keySet = lg.getFollowing().keySet(); 
                        Iterator iter = keySet.iterator();
                        while(iter.hasNext()){
                            String key = (String)iter.next();
                            int value = lg.getFollowing().get(key);
                            %>
                            <li><a href="/Instagrim/Images/<%=key%>"><%=key%></a>
                                (<a href="/Instagrim/CheckReminder/<%=key%>/<%=lg.getUsername()%>">New Pics</a>:<%=value%>)
                                <a href="/Instagrim/UnfollowUser/<%=key%>/<%=lg.getUsername()%>">Unfollow</a>
                            </li>
                            <%
                        }                            
                    }
                %>
                </div>
                <div class="addFollowing">
                    <form method="POST" action="/Instagrim/SearchUser">
                        <div class="getFriend" >Find a user: <input type="text" name="getFriend" class=text></div>
                        <input type="hidden" name="username" value="<%=lg.getUsername()%>" >
                        <div class="button"><input type="submit" value="Search" class="submit"></a></div>
                    </form>
                <%
                    Set<String> nameSet = (Set<String>) request.getAttribute("nameSet");
                    if(nameSet!=null){
                        
                            Iterator Iter = nameSet.iterator();
                            while(Iter.hasNext()){
                                String name = (String)Iter.next();
                                %><li><a href="/Instagrim/AddFollowing/<%=name%>/<%=lg.getUsername()%>"><%=name%></a></li><%
                            }
                        
                    }
                %>
                </div>
                <div class="recommend">
                    <a href="/Instagrim/RecommendList/<%=lg.getUsername()%>">Recommend user:</a><br>            
            <%
                java.util.LinkedList<Recommend> lsRecommends = (java.util.LinkedList<Recommend>) request.getAttribute("Recommends");
                if (lsRecommends != null) {
                    int i=0;
                    Iterator<Recommend> Iterator;
                    Iterator = lsRecommends.iterator();
                    while (Iterator.hasNext()) {
                        Recommend r = (Recommend) Iterator.next();
                        %><li><%=r.getName()%>: <%=r.getReason()%></li><%  
                        i++;
                        if(i==10){break;}
                }
                }      
            %>
                </div>    
            </div>
                
        </article>
    </body>
</html>
