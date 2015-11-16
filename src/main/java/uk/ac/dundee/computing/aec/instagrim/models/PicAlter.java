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
import uk.ac.dundee.computing.aec.instagrim.lib.Convertors;

/**
 *
 * @author Administrator
 */
public class PicAlter {
    
    Cluster cluster;

    public void PicAlter() {

    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public void getPicAlter(byte[] b, java.util.UUID picid) {
        Session session = cluster.connect("instagrimYan");
        String user = null;
        String type = null;
        String name = null;
        try {
            Convertors convertor = new Convertors();
            ResultSet rs = null;
            PreparedStatement ps = null;
            ps = session.prepare("select user,type,name from pics where picid =?");
            
            BoundStatement boundStatement = new BoundStatement(ps);
            rs = session.execute(boundStatement.bind(picid));
            if (rs.isExhausted()) {
                System.out.println("No Images returned");
            } else {
                for (Row row : rs) {
                    user = row.getString("user");
                    type = row.getString("type");
                    name = row.getString("name");
                }
            }
        } catch (Exception et) {
            System.out.println("Can't get Pic" + et);
        }
        session.close();
        PicModel tm = new PicModel();
        tm.setCluster(cluster);
        tm.insertPic(b, type, name, user,null);
        
    }
    
    
    
    
    
}
