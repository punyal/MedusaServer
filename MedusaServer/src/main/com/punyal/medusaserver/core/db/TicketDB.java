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

/**
 * TicketDB
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
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
    
    /**
     * Method to do MySQL Updates
     * @param sql request
     * @return true/false
     */
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
    
    /**
     * Remove all the previous data
     */
    public void resetDB() {
        mySQLupdate("truncate table `updates`");
        mySQLupdate("INSERT INTO `ticket_engine`.`updates` (`command`, `updatetime`) VALUES ('refresh', NOW());");
    }
    
    /**
     * Add a new Authenticator to the DB
     * @param address of the client
     * @param authenticator of the client
     * @return String of the new authenticator
     */
    public String newAuthenticator(InetAddress address, String authenticator) {
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((new Date()).getTime() + (AUTHENTICATION_CODE_TIMEOUT)));
        if (mySQLupdate("INSERT INTO `ticket_engine`.`users` (`address`, `authenticator`,  `expire_time`) VALUES ('"+address.toString().split("/")[1]+"', '"+authenticator+"',  '"+expireTime+"');") == 1)
            return expireTime;
        return null;
    }
    
    /**
     * Remove all expired Tickets and Authenticators
     */
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
    }
    
    /**
     * Deactivate an user by ID
     * @param id of the client
     */
    public void deactivate(int id) {
        mySQLupdate("UPDATE  `ticket_engine`.`users` SET  `active` =  '0' WHERE  `users`.`id` ="+id+";");
    }
    
    /**
     * Check if a specific user is already on the DB
     * @param userName
     * @return true/false
     */
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
    
    /**
     * Check if a Password is valid
     * @param address of the client
     * @param userName of the client
     * @param decodedPass of the client
     * @param cryptedPass of the client
     * @return true/false
     */
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
    
    /**
     * Set The userName by ID
     * @param id of the client
     * @param userName of the client
     */
    public void setUserNameByID(int id, String userName) {
        mySQLupdate("UPDATE `ticket_engine`.`users` SET `name` = '"+userName+"' WHERE `users`.`id` = "+id+";");
    }
        
    /**
     * Get the user info from the address and userName
     * @param address of the client
     * @param userName of the client
     * @return user info
     */
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
    
    /**
     * Get user info from Ticket
     * @param ticket of the client
     * @return user info
     */
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
    
    /**
     * Get user info from userName
     * @param name of the client
     * @return user info
     */
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
    
    /**
     * Set all user info on the DB
     * @param id of the client
     * @param address of the client
     * @param type of the client
     * @param info of the client
     * @param ticket of the client 
     * @param expireTime of the client
     * @return mysql error code
     */
    public int setAllData(int id, String address, String type, String info, String ticket, String expireTime) {
        return mySQLupdate("UPDATE `ticket_engine`.`users` SET `address` = '"+address+"', `type` = '"+type+"',`ticket` = '"+ticket+"'," +
            "`info` = '"+info+"', `expire_time` = '"+expireTime+"', `active` = true WHERE `users`.`id` = "+id+";");
    }
    
    /**
     * Clean expired Authenticator from the DB
     */
    public void cleanExpiredAuthenticators() {
        mySQLupdate("DELETE FROM `users` WHERE `name` IS NULL AND `active` = false;");
    }
    
    /**
     * Add User to the web interface
     * @param userName of the client
     * @param userInfo of the client
     * @param userType  of the client
     */
    public void webAddUser(String userName, String userInfo, String userType) {
        mySQLupdate("INSERT INTO `updates` (`command`, `name`, `info`, `type`, `updatetime`) VALUES ('new',"
                + " '"+userName+"', '"+userInfo+"', '"+userType+"',  NOW());");
    }
    
    /**
     * Remove User from the web interface
     * @param userName  of the client
     */
    public void webRemoveUser(String userName) {
        mySQLupdate("INSERT INTO `updates` (`command`, `name`, `updatetime`) VALUES ('delete', '"+userName+"',  NOW());");
    }
    
    /**
     * Add link from an specific user to other
     * @param from client's name
     * @param to other client's name
     */
    public void webAddLinkFrom(String from, String to) {
        mySQLupdate("INSERT INTO `links` (`from`, `to`) VALUES ('"+from+"', '"+to+"');");
        mySQLupdate("INSERT INTO `updates` (`command`, `from`, `to`, `updatetime`) VALUES ('new', '"+from+"', '"+to+"', NOW());");  
    }
    
    /**
     * Remove links form an specific user
     * @param userName of the client
     */
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
}