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
public class PicComment {
    
    private String piccomment=null;
    private String commentator=null;
    private java.util.UUID UUID=null;
    
    public void PicComment() {

    }
    
    public void setPicComment(String comment){
        this.piccomment = comment;
    }
    
    public String getPicComment(){
        return piccomment;
    }
    
    public void setCommentator(String commentator){
        this.commentator = commentator;
    }
    
    public String getCommentator(){
        return commentator;
    }
    public void setPicid(java.util.UUID UUID){
        this.UUID = UUID;
    }
    
    public String getPicSUUID(){
        return UUID.toString();
    }
}
