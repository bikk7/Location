package com.example.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Operation {
    public static void Insert(String fileName,String s, Connection con)
    {

        try {
            FileReader fr;
            fr = new FileReader(s);
            BufferedReader br = new BufferedReader(fr);
            String string=br.readLine();
            while(string!=null)
            {
                String ss[]=string.split(",");
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String sql="insert into "+'"'+fileName.substring(0,11)+ '"'+" values(?,?,?,?,?,?)";
                PreparedStatement pS=con.prepareStatement(sql);
                pS.setString(1,ss[0]);
                pS.setString(2,ss[1]);
                pS.setString(3,ss[2]);
                pS.setString(4,ss[3]);
                pS.setString(5,ss[4]);
                pS.setString(6,sdf.format(new Date()));
                pS.executeUpdate();
                string=br.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void Insert1(String s)
    {
       Connection con=Connet.getcon1();
        try {
                String sql="insert into Users(name)"+ " values(?)";
                PreparedStatement pS=con.prepareStatement(sql);
                pS.setString(1,s);
                pS.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
