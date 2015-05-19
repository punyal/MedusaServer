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

import static com.punyal.medusaserver.core.medusa.MedusaConstants.AUTHENTICATION_CODE_TIMEOUT;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.core.security.User;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class TicketDB {
    DBsql mySQL;
    
    public TicketDB(Status status, String server, String dbname, String user, String password) {
        mySQL = new DBsql(status, this.getClass().getSimpleName(), server, dbname, user, password);
        this.resetDB();
    }
    
    public void resetDB() {
        mySQL.Update("truncate table `updates`");
        mySQL.Update("INSERT INTO `ticket_engine`.`updates` (`command`, `updatetime`) VALUES ('refresh', NOW());");
    }
    
    public String newAuthenticator(InetAddress address, String authenticator) {
        String expireTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date((new Date()).getTime() + (AUTHENTICATION_CODE_TIMEOUT)));
        if (mySQL.Update("INSERT INTO `ticket_engine`.`users` (`address`, `authenticator`,  `expire_time`) VALUES ('"+address.toString().split("/")[1]+"', '"+authenticator+"',  '"+expireTime+"');") == 1)
            return expireTime;
        return null;
    }
    
    public ResultSet getExpired() {
        return mySQL.Query("SELECT * FROM `users` WHERE `active` = true AND `expire_time` < NOW();");
    }
    
    public void deactivate(String id) {
        mySQL.Update("UPDATE  `ticket_engine`.`users` SET  `active` =  '0' WHERE  `users`.`id` ="+id+";");
    }
    
    public ResultSet getAuthenticatorsByAddress(InetAddress address) {
        return mySQL.Query("SELECT `id`,`authenticator` FROM `users` WHERE `address` = '"+address.toString().split("/")[1]+"' AND `active` = true");
    }
    
    public int setUserNameByID(int id, String userName) {
        return mySQL.Update("UPDATE `ticket_engine`.`users` SET `name` = '"+userName+"' WHERE `users`.`id` = "+id+";");
    }
    
    public ResultSet getActiveUsersByAddressAndName(InetAddress address, String userName) {
        return mySQL.Query("SELECT * FROM `users` WHERE `address` = '"+address.toString().split("/")[1]+"' AND `name` = '"+userName+"' AND `active` = true");
    }
    
    public ResultSet getAllUsersByAddressAndName(InetAddress address, String userName) {
        return mySQL.Query("SELECT * FROM `users` WHERE `address` = '"+address.toString().split("/")[1]+"' AND `name` = '"+userName+"'");
    }
    
    public User getUserByTicket(String ticket) {
        ResultSet result = mySQL.Query("SELECT * FROM `users` WHERE `ticket` = '"+ticket+"' AND `active` = true");
        
        if (result != null) {
            try {
                if (result.next()) {
                    if (result.isLast()) {
                        try {
                            return new User(InetAddress.getByName(result.getString("address")),
                                    result.getString("name"),
                                    result.getString("type"),
                                    result.getString("info"),
                                    result.getString("connections"),
                                    result.getString("ticket"),
                                    result.getString("authenticator"),
                                    result.getString("expire_time") );
                        } catch (UnknownHostException ex) {
                            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else
                        System.out.println("more than one user with same ticket!!");
                }
            } catch (SQLException ex) {
                Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
        
    }
    
    public int setAllData(int id, String type, String info, String ticket, String expireTime) {
        return mySQL.Update("UPDATE `ticket_engine`.`users` SET `type` = '"+type+"',`ticket` = '"+ticket+"'," +
            "`info` = '"+info+"', `expire_time` = '"+expireTime+"', `active` = true WHERE `users`.`id` = "+id+";");
    }

    public boolean checkIfExist(String userName) {
        ResultSet result = mySQL.Query("SELECT id FROM `users` WHERE `name` ='"+userName+"';");
        try {
            if (result.next())
                return true;
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public int cleanExpiredAuthenticators() {
        return mySQL.Update("DELETE FROM `users` WHERE `name` IS NULL AND `active` = false;");
    }

    public void webAddUser(String userName, String userInfo, String userType) {
        mySQL.Update("INSERT INTO `updates` (`command`, `name`, `info`, `type`, `updatetime`) VALUES ('new',"
                + " '"+userName+"', '"+userInfo+"', '"+userType+"',  NOW());");
    }

    public void webRemoveUser(String userName) {
        mySQL.Update("INSERT INTO `updates` (`command`, `name`, `updatetime`) VALUES ('delete', '"+userName+"',  NOW());");
    }

    public void webRemoveLinkFrom(String userName) {
        ResultSet result = mySQL.Query("SELECT * FROM `links` WHERE `from` ='"+userName+"';");
        try {
            while (result.next()) {
                mySQL.Update("INSERT INTO `updates` (`command`, `from`, `to`, `updatetime`) VALUES ('delete', '"+userName+"', '"+result.getString("to")+"', NOW());");
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        mySQL.Update("DELETE FROM `links` WHERE `from` = '"+userName+"';");
    }
    
    public void webAddLinkFrom(String from, String to) {
        mySQL.Update("INSERT INTO `links` (`from`, `to`) VALUES ('"+from+"', '"+to+"');");
        mySQL.Update("INSERT INTO `updates` (`command`, `from`, `to`, `updatetime`) VALUES ('new', '"+from+"', '"+to+"', NOW());");  
    }
    
    
    
    
    
    
    
    
}