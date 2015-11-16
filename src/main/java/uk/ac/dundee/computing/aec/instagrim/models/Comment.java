package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (picid,interaction_time)
 ) WITH CLUSTERING ORDER BY (interaction_time DESC);
 * To manually generate a UUID use:
 * http://www.famkruithof.net/uuid/uuidgen
 */
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import uk.ac.dundee.computing.aec.instagrim.stores.PicComment;

public class Comment {
    
    Cluster cluster;
    
    public void Comment() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    
    public void insertComment(String comment, String commentator, java.util.UUID picid) {
        if(!"".equals(comment)){
            try {
                Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
                FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));            
                Session session = cluster.connect("instagrimYan");

                PreparedStatement psInsertComment = session.prepare("insert into Comments ( comment,commentator,picid,interaction_time) values(?,?,?,?)");
                PreparedStatement psInsertCommentToPic = session.prepare("insert into piccommentlist ( comment, picid, comment_added) values(?,?,?)");
                BoundStatement bsInsertComment = new BoundStatement(psInsertComment);
                BoundStatement bsInsertCommentToPic = new BoundStatement(psInsertCommentToPic);

                Date DateAdded = new Date();
                session.execute(bsInsertComment.bind(comment, commentator,picid, DateAdded));
                session.execute(bsInsertCommentToPic.bind(comment, picid, DateAdded));
                session.close();                       
            } catch (IOException ex) {
                System.out.println("Error --> " + ex);
            }
        }
    }
    
    public java.util.LinkedList<PicComment> getCommentsForPic(java.util.UUID picid) {
        java.util.LinkedList<PicComment> Comments = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select comment from piccommentlist where picid =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(picid));
        if (rs.isExhausted()) {
            System.out.println("No Comments returned");
            return null;
        } else {
            for (Row row : rs) {
                PicComment pc = new PicComment();
                String comment = row.getString("comment");
                System.out.println("comment" + comment);
                pc.setPicComment(comment);
                pc.setPicid(picid);
                String commentator=null;
                Date date=null;
                Session Session = cluster.connect("instagrimYan");
                PreparedStatement PS = Session.prepare("select commentator,interaction_time from Comments where comment =?");
                ResultSet RS = null;
                BoundStatement boundstatement = new BoundStatement(PS);
                RS = session.execute(boundstatement.bind(comment));
                for(Row r : RS){
                    commentator=r.getString("commentator");
                    date=r.getDate("interaction_time");
                    if(date!=null){pc.setCommentator(commentator);}
                }               
                Session.close();
                if(date!=null){                   
                    Comments.add(pc);
                }
            }
        }
        return Comments;
    }
    
    public void addCommentNum(java.util.UUID picid){
        int messageNum = 0;
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select messagenum from Pics where picid =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        picid));
        if (rs.isExhausted()) {
            System.out.println("No messagenum returned");
        } else {
            for (Row row : rs) {
                messageNum = row.getInt("messagenum");
            }
        }
        messageNum++;
        session.close();
        Session Session = cluster.connect("instagrimYan");
        PreparedStatement PS = Session.prepare("insert into Pics (picid,messagenum) Values(?,?)");
        BoundStatement bs = new BoundStatement(PS);
        Session.execute(bs.bind(picid,messageNum));
        Session.close();
    }
    
    public void removeComment(java.util.UUID picid,String comment){
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("delete from comments where comment =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(comment));
        }
        Date date = new Date();
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("select comment_added from piccommentlist where picid =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));
            for (Row row : rs) {
                    date = row.getDate("comment_added");
                }
        }
        try (Session s = cluster.connect("instagrimYan")) {
            PreparedStatement PS = s.prepare("delete from piccommentlist where picid =? and comment_added =?");
            ResultSet RS = null;
            BoundStatement boundstatement = new BoundStatement(PS);
            RS = s.execute(boundstatement.bind(picid,date));
        }
    }
    
    public void reduceCommentNum(java.util.UUID picid){
        int messageNum = 0;
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select messagenum from Pics where picid =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        picid));
        if (rs.isExhausted()) {
            System.out.println("No messagenum returned");
        } else {
            for (Row row : rs) {
                messageNum = row.getInt("messagenum");
            }
        }
        messageNum--;
        session.close();
        Session Session = cluster.connect("instagrimYan");
        PreparedStatement PS = Session.prepare("insert into Pics (picid,messagenum) Values(?,?)");
        BoundStatement bs = new BoundStatement(PS);
        Session.execute(bs.bind(picid,messageNum));
        Session.close();
    }
    
}
