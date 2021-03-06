/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.stores;

import com.datastax.driver.core.utils.Bytes;
import java.nio.ByteBuffer;

/**
 *
 * @author Administrator
 */
public class Pic {

    private ByteBuffer bImage = null;
    private int length;
    private String type;
    private java.util.UUID UUID=null;
    private int thumbnum=0;
    private int messagenum=0;
    private String picTime=null;
    private String user=null;
    
    public void Pic() {

    }
    public void setUUID(java.util.UUID UUID){
        this.UUID =UUID;
    }
    public String getSUUID(){
        return UUID.toString();
    }
    public void setPic(ByteBuffer bImage, int length,String type) {
        this.bImage = bImage;
        this.length = length;
        this.type=type;
    }

    public ByteBuffer getBuffer() {
        return bImage;
    }

    public int getLength() {
        return length;
    }
    
    public String getType(){
        return type;
    }

    public byte[] getBytes() {         
        byte image[] = Bytes.getArray(bImage);
        return image;
    }
    
    public int getThumbNum(){
        return thumbnum;
    }    
    public void setThumbNum(int num){
        this.thumbnum=num;
    }
    
    public String getTime(){
        return picTime;
    }   
    public void setTime(String time){
        this.picTime=time;
    }
    
    public String getUser(){
        return user;
    }    
    public void setUser(String user){
        this.user=user;
    }
    
    public int getMessageNum(){
        return messagenum;
    }   
    public void setMessageNum(int num){
        this.messagenum=num;
    }

}
