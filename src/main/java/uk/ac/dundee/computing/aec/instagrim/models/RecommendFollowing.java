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
import java.util.Date;
import uk.ac.dundee.computing.aec.instagrim.stores.Recommend;

/**
 *
 * @author Administrator
 */
public class RecommendFollowing {
    
    Cluster cluster;

    public void RecommendFollowing() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public void InsertReference(String user, java.util.UUID picid,String reason){                     
        String name = null;
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement ps = session.prepare("select user from Pics where picid =?");
            ResultSet rs = null;
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(picid));
            for (Row row : rs) {
                name = row.getString("user");
            }
        }    
        
        try (Session session = cluster.connect("instagrimYan")) {
            PreparedStatement psInsertRecommend = session.prepare("insert into followrecommendlist (user,recommend,reason,recommend_added) values(?,?,?,?)");
            BoundStatement bsInsertRecommend = new BoundStatement(psInsertRecommend);
            Date DateAdded = new Date();
            session.execute(bsInsertRecommend.bind(user, name, reason, DateAdded));
        }           
    }
    
    public java.util.LinkedList<Recommend> getRecommendsForUser(String User) {
        java.util.LinkedList<Recommend> Recommends = new java.util.LinkedList<>();
        Session session = cluster.connect("instagrimYan");
        PreparedStatement ps = session.prepare("select recommend,reason from followrecommendlist where user =?");
        ResultSet rs = null;
        BoundStatement boundStatement = new BoundStatement(ps);
        rs = session.execute(boundStatement.bind(User));
        if (rs.isExhausted()) {
            System.out.println("No recommends returned");
            return null;
        } else {
            for (Row row : rs) {
                Recommend refriend = new Recommend();
                String name = row.getString("recommend");
                String reason = row.getString("reason");

                refriend.setName(name);
                refriend.setReason(reason);

                Recommends.add(refriend);

            }
        }
        return Recommends;
    }
    
    
    
}
