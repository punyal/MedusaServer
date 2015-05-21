/*******************************************************************************
 * MedusaServer - Multi Protocol Access Control Server
 * 
 * Copyright (c) 2015 - Pablo Puñal Pereira <pablo@punyal.com>
 *                      EISLAB : Luleå University of Technology
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 ******************************************************************************/
package com.punyal.medusaserver.core.db;

import static com.punyal.medusaserver.core.medusa.Configuration.*;
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.core.security.Cryptonizer;
import com.punyal.medusaserver.core.security.User;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TicketDB {
    private final String server;
    private final String dbname;
    private final String user;
    private final String password;
    
    public TicketDB(Status status, String server, String dbname, String user, String password) {
        status.addNewDBStatus(this.getClass().getSimpleName());
        this.server = server;
        this.dbname = dbname;
        this.user = user;
        this.password = password;
        this.resetDB();
    }
    
    private int mySQLupdate(String sql) {
        int toReturn = 0;
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement()) {
                toReturn = statement.executeUpdate(sql);
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    public void resetDB() {
        mySQLupdate("truncate table `updates`");
        mySQLupdate("INSERT INTO `ticket_engine`.`updates` (`command`, `updatetime`) VALUES ('refresh', NOW());");
    }
    
    public String newAuthenticator(InetAddress address, String authenticator) {
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((new Date()).getTime() + (AUTHENTICATION_CODE_TIMEOUT)));
        if (mySQLupdate("INSERT INTO `ticket_engine`.`users` (`address`, `authenticator`,  `expire_time`) VALUES ('"+address.toString().split("/")[1]+"', '"+authenticator+"',  '"+expireTime+"');") == 1)
            return expireTime;
        return null;
    }
    
    public void removeExpired() {        
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `active` = true AND `expire_time` < NOW();")) {
                
                while (resultSet.next()) {
                    this.deactivate(resultSet.getInt("id"));
                    if (resultSet.getString("name") != null) { // check if it's an user or a temp authenticator code
                        this.webRemoveUser(resultSet.getString("name"));
                        this.webRemoveLinkFrom(resultSet.getString("name"));
                    }
                }
                this.cleanExpiredAuthenticators();
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
         /*
            try {
                user = ticketDB.getExpired();
                if (user != null) {
                    ticketDB.deactivate(user.i);
                        while (result.next()) {
                            //System.out.println("Expired: "+result.getString("id"));
                            ticketDB.deactivate(result.getString("id"));
                            if (result.getString("name") != null) {
                                ticketDB.webRemoveUser(result.getString("name"));
                                ticketDB.webRemoveLinkFrom(result.getString("name"));
                            }
                                
                        }
                    ticketDB.cleanExpiredAuthenticators();
                }
            } catch (SQLException ex) {
                Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
            */
        //return mySQL.Query("SELECT * FROM `users` WHERE `active` = true AND `expire_time` < NOW();");
    }
    
    public void deactivate(int id) {
        mySQLupdate("UPDATE  `ticket_engine`.`users` SET  `active` =  '0' WHERE  `users`.`id` ="+id+";");
    }
    
    private boolean checkIfExist(String userName) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id FROM `users` WHERE `name` ='"+userName+"';");
            if (resultSet.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean checkPass(InetAddress address, String userName, String decodedPass, String cryptedPass) {
        boolean toReturn = false;
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT `id`,`authenticator` FROM `users` WHERE `address` = '"+address.toString().split("/")[1]+"' AND `active` = true")) {
                
                while (resultSet.next()) {
                    if (Cryptonizer.encryptCoAP(AUTHENTICATION_SECRET_KEY, resultSet.getString("authenticator"), decodedPass).equals(cryptedPass)) {
                        if (!this.checkIfExist(userName))
                            this.setUserNameByID(resultSet.getInt("id"), userName);
                        toReturn = true;
                    }
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toReturn;
    }
    
    public void setUserNameByID(int id, String userName) {
        mySQLupdate("UPDATE `ticket_engine`.`users` SET `name` = '"+userName+"' WHERE `users`.`id` = "+id+";");
    }
        
    public User getUser(InetAddress address, String userName) {
        User toReturn = null;
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `address` = '"+address.toString().split("/")[1]+"' AND `name` = '"+userName+"'")) {
                
                if (resultSet.next())
                    if (resultSet.isLast())
                        toReturn = new User(resultSet.getInt("id"),
                                            address,
                                            resultSet.getString("name"),
                                            resultSet.getString("type"),
                                            resultSet.getString("info"),
                                            resultSet.getString("connections"),
                                            resultSet.getString("ticket"),
                                            resultSet.getString("authenticator"),
                                            resultSet.getString("expire_time"),
                                            resultSet.getBoolean("active"));
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    public User getUserByTicket(String ticket) {
        User toReturn = null;
        
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `ticket` = '"+ticket+"' AND `active` = true")) {
                
                if (resultSet.next())
                    if (resultSet.isLast()) {
                        InetAddress address = null;
                        try {
                            address = InetAddress.getByName(resultSet.getString("address"));
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        toReturn = new User(resultSet.getInt("id"),
                                            address,
                                            resultSet.getString("name"),
                                            resultSet.getString("type"),
                                            resultSet.getString("info"),
                                            resultSet.getString("connections"),
                                            resultSet.getString("ticket"),
                                            resultSet.getString("authenticator"),
                                            resultSet.getString("expire_time"),
                                            resultSet.getBoolean("active"));
                    }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    public User getUserByName(String name) {
        User toReturn = null;
        
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM `users` WHERE `name` = '"+name+"'")) {
                
                if (resultSet.next())
                    if (resultSet.isLast()) {
                        InetAddress address = null;
                        try {
                            address = InetAddress.getByName(resultSet.getString("address"));
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        toReturn = new User(resultSet.getInt("id"),
                                            address,
                                            resultSet.getString("name"),
                                            resultSet.getString("type"),
                                            resultSet.getString("info"),
                                            resultSet.getString("connections"),
                                            resultSet.getString("ticket"),
                                            resultSet.getString("authenticator"),
                                            resultSet.getString("expire_time"),
                                            resultSet.getBoolean("active"));
                        
                    }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return toReturn;
    }
    
    public int setAllData(int id, String address, String type, String info, String ticket, String expireTime) {
        return mySQLupdate("UPDATE `ticket_engine`.`users` SET `address` = '"+address+"', `type` = '"+type+"',`ticket` = '"+ticket+"'," +
            "`info` = '"+info+"', `expire_time` = '"+expireTime+"', `active` = true WHERE `users`.`id` = "+id+";");
    }

    public void cleanExpiredAuthenticators() {
        mySQLupdate("DELETE FROM `users` WHERE `name` IS NULL AND `active` = false;");
    }

    public void webAddUser(String userName, String userInfo, String userType) {
        mySQLupdate("INSERT INTO `updates` (`command`, `name`, `info`, `type`, `updatetime`) VALUES ('new',"
                + " '"+userName+"', '"+userInfo+"', '"+userType+"',  NOW());");
    }

    public void webRemoveUser(String userName) {
        mySQLupdate("INSERT INTO `updates` (`command`, `name`, `updatetime`) VALUES ('delete', '"+userName+"',  NOW());");
    }

    public void webRemoveLinkFrom(String userName) {
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery("SELECT * FROM `links` WHERE `from` ='"+userName+"';")) {
                while (resultSet.next())
                    mySQLupdate("INSERT INTO `updates` (`command`, `from`, `to`, `updatetime`) VALUES ('delete', '"+userName+"', '"+resultSet.getString("to")+"', NOW());");
                mySQLupdate("DELETE FROM `links` WHERE `from` = '"+userName+"';");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void webAddLinkFrom(String from, String to) {
        mySQLupdate("INSERT INTO `links` (`from`, `to`) VALUES ('"+from+"', '"+to+"');");
        mySQLupdate("INSERT INTO `updates` (`command`, `from`, `to`, `updatetime`) VALUES ('new', '"+from+"', '"+to+"', NOW());");  
    }
    
    
    
    
    
    
    
    
}