/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

/**
 *
 * @author Administrator
 */
public class Recommend {
    
    private String name = null;
    private String reason = null;
    
    public void Recommend() {

    }
    
    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
    
    public void setReason(String reason){
        this.reason=reason;
    }
    public String getReason(){
        return this.reason;
    }
    
}
