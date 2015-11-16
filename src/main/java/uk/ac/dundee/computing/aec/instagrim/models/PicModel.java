package uk.ac.dundee.computing.aec.instagrim.models;

/*
 * Expects a cassandra columnfamily defined as
 * use keyspace2;
 CREATE TABLE Tweets (
 user varchar,
 interaction_time timeuuid,
 tweet varchar,
 PRIMARY KEY (user,interaction_time)
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
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import static org.imgscalr.Scalr.*;
import org.imgscalr.Scalr.Method;

import uk.ac.dundee.computing.aec.instagrim.lib.*;
import uk.ac.dundee.computing.aec.instagrim.stores.Pic;

public class PicModel {

    Cluster cluster;

    public void PicModel() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public void insertReminder(String user) {
        Set<String> Fset = new HashSet<>();

        Session s = cluster.connect("instagrimYan");
        PreparedStatement PS = s.prepare("select follower from userprofiles where login =?");
        ResultSet rs = null;
        BoundStatement boundstatement = new BoundStatement(PS);
        rs = s.execute(boundstatement.bind(user));
        for (Row row : rs) {
            Fset=row.getSet("follower", String.class);
        }
        for(String str:Fset){
            Map<String,Integer> Umap = new HashMap<>();
            Map<String,Integer> umap = new HashMap<>();
            Session S = cluster.connect("instagrimYan");
            PreparedStatement pS = S.prepare("select following from userprofiles where login =?");
            ResultSet RS = null;
            BoundStatement boundStatement = new BoundStatement(pS);
            RS = S.execute(boundStatement.bind(str));
            for (Row row : RS) {
                Umap=row.getMap("following", String.class, Integer.class);               
            }
            int num = Umap.get(user);
            num++;
            umap.putAll(Umap);
            umap.put(user, num);
            S.close();
                
            Session session = cluster.connect("instagrimYan");
            PreparedStatement psFriends = session.prepare("insert into userprofiles (login,following) values(?,?)");
            BoundStatement bsFriends = new BoundStatement(psFriends);
            session.execute(bsFriends.bind(str, umap));
            session.close();
        }
    }
    
    public void RemovePic(java.util.UUID picid, String username){
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("delete from Pics where picid =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(picid));
        }
        Date date = new Date();
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("select pic_added from userpiclist where user =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            username));
            for (Row row : rs) {
                    date = row.getDate("pic_added");
                }
        }
        try (Session s = cluster.connect("instagrimYan")) {
            PreparedStatement PS = s.prepare("delete from userpiclist where user =? and pic_added =?");
            ResultSet RS = null;
            BoundStatement boundstatement = new BoundStatement(PS);
            RS = s.execute(boundstatement.bind(username,date));
        }

    }
    
    public void insertThumb(java.util.UUID picid){
        int thumbNum = 0;
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("select thumbnum from Pics where picid =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));
            if (rs.isExhausted()) {
                System.out.println("No thumbnum returned");
            } else {
                for (Row row : rs) {
                    thumbNum = row.getInt("thumbnum");
                }
            }
            thumbNum++;
        }
        try (Session Session = cluster.connect("instagrimYan")) {
            PreparedStatement PS = Session.prepare("insert into Pics (picid,thumbnum) Values(?,?)");
            BoundStatement bs = new BoundStatement(PS);
            Session.execute(bs.bind(picid,thumbNum));
        }
    }

    public void insertPic(byte[] b, String type, String name, String user,String message) {
        try {
            Convertors convertor = new Convertors();

            String types[]=Convertors.SplitFiletype(type);
            ByteBuffer buffer = ByteBuffer.wrap(b);
            int length = b.length;
            java.util.UUID picid = convertor.getTimeUUID();
            
            //The following is a quick and dirty way of doing this, will fill the disk quickly !
            Boolean success = (new File("/var/tmp/instagrim/")).mkdirs();
            FileOutputStream output = new FileOutputStream(new File("/var/tmp/instagrim/" + picid));

            output.write(b);
            byte []  thumbb = picresize(picid.toString(),types[1]);
            int thumblength= thumbb.length;
            ByteBuffer thumbbuf=ByteBuffer.wrap(thumbb);
            byte[] processedb = picdecolour(picid.toString(),types[1]);
            ByteBuffer processedbuf=ByteBuffer.wrap(processedb);
            int processedlength=processedb.length;
            Session session = cluster.connect("instagrimYan");

            PreparedStatement psInsertPic = session.prepare("insert into pics ( picid, image,thumb,processed, user, interaction_time,imagelength,thumblength,processedlength,type,name) values(?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement psInsertPicToUser = session.prepare("insert into userpiclist ( picid, user, pic_added) values(?,?,?)");
            BoundStatement bsInsertPic = new BoundStatement(psInsertPic);
            BoundStatement bsInsertPicToUser = new BoundStatement(psInsertPicToUser);

            Date DateAdded = new Date();
            session.execute(bsInsertPic.bind(picid, buffer, thumbbuf,processedbuf, user, DateAdded, length,thumblength,processedlength, type, name));
            session.execute(bsInsertPicToUser.bind(picid, user, DateAdded));
            session.close();
            
            if(!"".equals(message)&&message!=null){
                Comment cm = new Comment();
                cm.setCluster(cluster);
                cm.insertComment(message,user,picid);                
                cm.addCommentNum(picid);
            }
            
            insertReminder(user);

        } catch (IOException ex) {
            System.out.println("Error --> " + ex);
        }
    }

    public byte[] picresize(String picid,String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage thumbnail = createThumbnail(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, type, baos);
            baos.flush();
            
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }
    
    public byte[] picdecolour(String picid,String type) {
        try {
            BufferedImage BI = ImageIO.read(new File("/var/tmp/instagrim/" + picid));
            BufferedImage processed = createProcessed(BI);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(processed, type, baos);
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            baos.close();
            return imageInByte;
        } catch (IOException et) {

        }
        return null;
    }

    public static BufferedImage createThumbnail(BufferedImage img) {
        img = resize(img, Method.SPEED, 250, OP_ANTIALIAS, OP_GRAYSCALE);
        // Let's add a little border before we return result.
        return pad(img, 2);
    }
    
   public static BufferedImage createProcessed(BufferedImage img) {
        int Width=img.getWidth()-1;
        img = resize(img, Method.SPEED, Width, OP_ANTIALIAS, OP_GRAYSCALE);
        return pad(img, 4);
    }
   
   public java.util.LinkedList<Pic> getAllPics() {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select picid from userpiclist");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind());
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                int thumbNum=0;
                int messageNum=0;
                String user=null;
                Date picTime=null;
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM); 
                
                Session Session = cluster.connect("instagrimYan");
                PreparedStatement PS = Session.prepare("select thumbnum,messagenum,interaction_time,user from Pics where picid =?");
                ResultSet RS = null;
                BoundStatement boundstatement = new BoundStatement(PS);
                RS = session.execute(boundstatement.bind(UUID));
                for(Row r : RS){
                    thumbNum=r.getInt("thumbnum");
                    messageNum=r.getInt("messagenum");
                    picTime=r.getDate("interaction_time");
                    user=r.getString("user");
                    if(picTime!=null){
                        pic.setThumbNum(thumbNum);
                        pic.setMessageNum(messageNum);
                        pic.setTime(df.format(picTime));
                        pic.setUser(user);                        
                    }
                }                
                Session.close();                
                if(picTime!=null){Pics.add(pic);}
            }
        }
        return Pics;
    }
   
    public java.util.LinkedList<Pic> getPicsForUser(String User) {
        java.util.LinkedList<Pic> Pics = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select picid from userpiclist where user =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute( // this is where the query is executed
                boundStatement.bind( // here you are binding the 'boundStatement'
                        User));
        if (rs.isExhausted()) {
            System.out.println("No Images returned");
            return null;
        } else {
            for (Row row : rs) {
                Pic pic = new Pic();
                java.util.UUID UUID = row.getUUID("picid");
                System.out.println("UUID" + UUID.toString());
                pic.setUUID(UUID);
                int thumbNum=0;
                int messageNum=0;
                String user=null;
                Date picTime=null;
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM); 
                
                Session Session = cluster.connect("instagrimYan");
                PreparedStatement PS = Session.prepare("select thumbnum,messagenum,interaction_time,user from Pics where picid =?");
                ResultSet RS = null;
                BoundStatement boundstatement = new BoundStatement(PS);
                RS = session.execute(boundstatement.bind(UUID));
                for(Row r : RS){                    
                    thumbNum=r.getInt("thumbnum");
                    messageNum=r.getInt("messagenum");
                    picTime=r.getDate("interaction_time");
                    user=r.getString("user");
                    if(picTime!=null){
                        pic.setThumbNum(thumbNum);
                        pic.setMessageNum(messageNum);
                        pic.setTime(df.format(picTime));
                        pic.setUser(user);                   
                    }
                }                
                Session.close();
                if(picTime!=null){Pics.add(pic);}
            }
        }
        return Pics;
    }

    public Pic getPic(int image_type, java.util.UUID picid) {
        Session session = cluster.connect("instagrimYan");
        ByteBuffer bImage = null;
        String type = null;
        int length = 0;
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
         
            if (image_type == Convertors.DISPLAY_IMAGE) {
                
                ps = session.prepare("select image,imagelength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_THUMB) {
                ps = session.prepare("select thumb,imagelength,thumblength,type from pics where picid =?");
            } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                ps = session.prepare("select processed,processedlength,type from pics where picid =?");
            }
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute( // this is where the query is executed
                    boundStatement.bind( // here you are binding the 'boundStatement'
                            picid));

            if (rs.isExhausted()) {
                System.out.println("No Images returned");
                return null;
            } else {
                for (Row row : rs) {
                    if (image_type == Convertors.DISPLAY_IMAGE) {
                        bImage = row.getBytes("image");
                        length = row.getInt("imagelength");
                    } else if (image_type == Convertors.DISPLAY_THUMB) {
                        bImage = row.getBytes("thumb");
                        length = row.getInt("thumblength");
                
                    } else if (image_type == Convertors.DISPLAY_PROCESSED) {
                        bImage = row.getBytes("processed");
                        length = row.getInt("processedlength");
                    }
                    
                    type = row.getString("type");

                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
            return null;
        }
        session.close();
        Pic p = new Pic();
        p.setPic(bImage, length, type);

        return p;

    }

}
