/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.instagrim.models;

import com.datastax.driver.core.Cluster;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Administrator
 */
public class UploadTimer{
    
    Cluster cluster;
    Timer timer;
    
    public void UploadTimer(){
        
    }
    
    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }
    
    public void setUpload(final byte[] b, final String type, final String name, final String user,final String message,
            String hour,String minute,String second){
        TimerTask task = new TimerTask(){            
            @Override
            public void run(){
                PicModel tm = new PicModel();
                tm.setCluster(cluster);
                tm.insertPic(b, type, name, user, message);
                timer.cancel(); 
            }
        };
        Timer t = new Timer();
        Date time = setTime(hour,minute,second);
        t.schedule(task, time); 
    }
    
    public Date setTime(String hour,String minute,String second){
        int ihour = 0;
        int iminute = 0;
        int isecond = 0;
        try {    
            ihour = Integer.parseInt(hour);
            iminute = Integer.parseInt(minute);
            isecond = Integer.parseInt(second);
        } catch (NumberFormatException e){
        }        
        Calendar calendar = Calendar.getInstance();        
        calendar.set(Calendar.HOUR_OF_DAY, ihour);
        calendar.set(Calendar.MINUTE, iminute);
        calendar.set(Calendar.SECOND, isecond);
        
        Date time = calendar.getTime();
        
        return time;
    }
    
}
