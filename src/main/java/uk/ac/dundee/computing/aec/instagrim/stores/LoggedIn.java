/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.dundee.computing.aec.instagrim.stores;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Administrator
 */
public class LoggedIn {
    boolean logedin=false;
    String Username=null;
    String Gender=null;
    String First_name=null;
    String Last_name=null;
    String Email=null;
    String userImgNum=null;
    Set<String> email = new HashSet<>();
    Set<String> follower = new HashSet<>();
    Map<String,Integer> following = new HashMap<>();
    
    public void LogedIn(){
        
    }
    
    public void setUsername(String name){
        this.Username=name;
    }
    public String getUsername(){
        return Username;
    }
    public void setLogedin(){
        logedin=true;
    }
    public void setLogedout(){
        logedin=false;
    }
    
    public void setLoginState(boolean logedin){
        this.logedin=logedin;
    }
    public boolean getlogedin(){
        return logedin;
    }
    
    public void setGender(String gender){
        this.Gender=gender;
    }
    public String getGender(){
        return Gender;
    }
    
    public void setFirst_name(String first_name){
        this.First_name=first_name;
    }
    public String getFirst_name(){
        return First_name;
    }
    
    public void setLast_name(String last_name){
        this.Last_name=last_name;
    }
    public String getLast_name(){
        return Last_name;
    }
    
    public void setEmail(Set<String> email){
        this.email=email;
    }
    public Set<String> getEmail(){
        return email;
    }
    
    public void setFollowing(Map<String,Integer> following){
        this.following=following;
    }
    public Map<String,Integer> getFollowing(){
        return following;
    }
    
    public void setFollower(Set<String> follower){
        this.follower=follower;
    }
    public Set<String> getFollower(){
        return follower;
    }
    
    public void setUserImgNum(String userImgNum){
        this.userImgNum=userImgNum;
    }
    public String getUserImgNum(){
        return userImgNum;
    }

}
