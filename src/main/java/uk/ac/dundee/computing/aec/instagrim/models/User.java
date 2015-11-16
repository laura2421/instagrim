/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.LoggedIn;

/**
 *
 * @author Administrator
 */
public class User {
    Cluster cluster;
    public User(){
        
    }
    
    public boolean CheckUser(String username){
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select login from userprofiles");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind());
        if (rs.isExhausted()) {
            System.out.println("No user returned");
            return true;
        }else{
            for(Row row : rs){
                String existName = row.getString("login");
                if(existName.equals(username)) {
                    return false;
                }
            }
            return true;
        }
    }
    
    public boolean RegisterUser(String username, String Password, String email, String gender){
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        
        Set<String> Eset = new HashSet<>();
        
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select email from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(username));
        for (Row row : rs) {
            Eset=row.getSet("email", String.class);
        }
        if(!"".equals(email)){
            Eset.add(email);
        }
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,password,gender,email) Values(?,?,?,?)");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username,EncodedPassword,gender,Eset));
        //We are assuming this always works.  Also a transaction would be good here !
        
        return true;
    }
    
    public boolean EditUser(String first_name, String last_name, String gender, String email, String Password, String username){
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        Set<String> Eset = new HashSet<>();
        Set<String> eset = new HashSet<>();
        String FN = null;
        String LN = null;
        String GE = null;
        String PW = null;
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select first_name,last_name,password,gender,email from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(username));
        for (Row row : rs) {
            Eset=row.getSet("email", String.class);
            FN=row.getString("first_name");
            LN=row.getString("last_name");
            GE=row.getString("gender");
            PW=row.getString("password");
        }
        if(!"".equals(first_name)){FN=first_name;}
        if(!"".equals(last_name)){LN=last_name;}
        if((!"".equals(gender))&&(gender!=null)){GE=gender;}
        if(!"".equals(Password)){PW=EncodedPassword;}
        if(!"".equals(email)){eset.add(email);}       
        eset.addAll(Eset);
        
          
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("insert into userprofiles (login,first_name,last_name,password,gender,email) Values(?,?,?,?,?,?)");
       
        BoundStatement boundStatement = new BoundStatement(ps);
        session.execute(boundStatement.bind(username,FN,LN,PW,GE,eset));
        
        return true;
    }
    
    public LoggedIn getProfiles(String username){
        LoggedIn lg= new LoggedIn();
        String gender=null;
        String first_name=null;
        String last_name=null;
        Set<String> email = new HashSet<>();
        try (Session s = cluster.connect("instagrimYan")) {            
            PreparedStatement ps = s.prepare("select gender,first_name,last_name,email from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = s.execute(boundStatement.bind(username));
            for (Row row : rs) {
                gender=row.getString("gender");
                first_name=row.getString("first_name");
                last_name=row.getString("last_name");
                email=row.getSet("email",String.class);
            }
            lg.setGender(gender);
            lg.setFirst_name(first_name);
            lg.setLast_name(last_name);    
            lg.setUsername(username);
            lg.setEmail(email);
        }
        return lg;
    }
    
    public boolean RemoveEmail(String email,String username){
        Set<String> Eset = new HashSet<>();
        Set<String> eset = new HashSet<>();
        
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select email from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(username));
        for (Row row : rs) {
            Eset=row.getSet("email", String.class);
        }              
        
        eset.addAll(Eset);
        if(!"".equals(email)){eset.remove(email);} 
        
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("insert into userprofiles (login,email) Values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(username,eset));
            return true;
        }   
    }
    
    public boolean RemoveFollowing(String following, String username){
        Map<String,Integer> Fmap = new HashMap<>();
        Map<String,Integer> fmap = new HashMap<>();
        
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select following from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(username));
        for (Row row : rs) {
            Fmap=row.getMap("following", String.class, Integer.class);
        }
             
        fmap.putAll(Fmap);
        if(!"".equals(following)){fmap.remove(following);} 
        
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("insert into userprofiles (login,following) Values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(username,fmap));
            return true;
        }
    }
    
    public boolean RemoveFollower(String follower,String username){
        Set<String> Fset = new HashSet<>();
        Set<String> fset = new HashSet<>();
        
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select follower from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(username));
        for (Row row : rs) {
            Fset=row.getSet("follower", String.class);
        }              
        
        fset.addAll(Fset);
        if(!"".equals(follower)){fset.remove(follower);} 
        
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("insert into userprofiles (login,follower) Values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(username,fset));
            return true;
        }   
    }
    
    public boolean addFollowing(String friendname, String username){        
        Map<String,Integer> Fmap = new HashMap<>();
        Map<String,Integer> fmap = new HashMap<>();
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select following from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(username));
        for (Row row : rs) {
            Fmap=row.getMap("following", String.class, Integer.class);
        }
        if(!"".equals(friendname)){fmap.put(friendname, 0);}       
        fmap.putAll(Fmap);
        
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("insert into userprofiles (login,following) Values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(username,fmap));
            return true;
        }
    }
    
    public boolean addFollower(String friendname, String username){        
        Set<String> Fset = new HashSet<>();
        Set<String> fset = new HashSet<>();
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select follower from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(friendname));
        for (Row row : rs) {
            Fset=row.getSet("follower", String.class);
        }
        if(!"".equals(friendname)){fset.add(username);}       
        fset.addAll(Fset);
        
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("insert into userprofiles (login,follower) Values(?,?)");
            BoundStatement boundStatement = new BoundStatement(ps);
            session.execute(boundStatement.bind(friendname,fset));
            return true;
        }        
    }
    
    public boolean IsValidUser(String username, String Password){
        AeSimpleSHA1 sha1handler=  new AeSimpleSHA1();
        String EncodedPassword=null;
        try {
            EncodedPassword= sha1handler.SHA1(Password);
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException et){
            System.out.println("Can't check your password");
            return false;
        }
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select password from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        username));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return false;
        } else {
            for (Row row : rs) {
               
                String StoredPass = row.getString("password");
                if (StoredPass.compareTo(EncodedPassword) == 0)
                    return true;
            }
        }
      
    return false;  
    }
    
    public void CleanReminders(Map<String,Integer> friends, String User, String Friend){
        Map<String,Integer> fmap = new HashMap<>();
        fmap.putAll(friends);
        fmap.put(Friend, 0);
        Session session = cluster.connect("instagrimYan");
        PreparedStatement psFriends = session.prepare("insert into userprofiles (login,following) values(?,?)");
        BoundStatement bsFriends = new BoundStatement(psFriends);
        session.execute(bsFriends.bind(User, fmap));

    }
    
    public Set SearchUser(String user, String name){
        Set<String> nameSet = new HashSet<>();
        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select login from userprofiles");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind());
        if (rs.isExhausted()) {
            System.out.println("No user returned");
            return nameSet;
        }else{
            for(Row row : rs){
                String existName = row.getString("login");
                if(existName.contains(name)) {
                    nameSet.add(existName);
                }
            }
            nameSet.remove(user);
            return nameSet;
        }
    }
    
    public Set UserFollower(String User){        
        Set<String> follower = new HashSet<>();
        try (Session s = cluster.connect("instagrimYan")) {            
            PreparedStatement ps = s.prepare("select follower from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = s.execute(boundStatement.bind(User));
            for (Row row : rs) {
                follower=row.getSet("follower", String.class);
            }
        }
        return follower;
    }
    
    public Map UserFollowing(String User){        
        Map<String,Integer> friends = new HashMap<>();
        try (Session s = cluster.connect("instagrimYan")) {            
            PreparedStatement ps = s.prepare("select following from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = s.execute(boundStatement.bind(User));
            for (Row row : rs) {
                friends=row.getMap("following", String.class, Integer.class);
            }         
        }   
        return friends;
    }
    
    public String UserImgNum(String username){        
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select userImgNum from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(username));
        for (Row row : rs) {   
            String num = row.getString("userImgNum");   
            return num;
            }    
    return null;   
    }
    
    public String UserGender(String username){      
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("select gender from userprofiles where login =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(username));
            for (Row row : rs) {
                String gender = row.getString("gender");
                return gender;
            }
        }
    return null;   
    }
    
    
    public java.util.UUID UserImgID(String username){    
        java.util.UUID userImg=null;
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select userImg from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(username));
        for (Row row : rs) {   
            userImg = row.getUUID("userImg");        
            return userImg;
            }    
    return userImg;   
    }
    
       public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

    
}
