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
package com.punyal.medusaserver.core.security;

import com.punyal.medusaserver.core.GlobalVars;
import com.punyal.medusaserver.core.db.AuthenticationDB;
import com.punyal.medusaserver.core.db.TicketDB;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.utils.UnitConversion;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class TicketEngine extends Thread{
    private static final Logger LOGGER = Logger.getLogger(TicketEngine.class.getCanonicalName());
    private boolean running;
    private final Randomizer randomizer;
    private final AuthenticationDB authDB;
    private final TicketDB ticketDB;
    
    public TicketEngine(GlobalVars globalVars) {
        running = false;
        randomizer = new Randomizer();
        authDB = globalVars.getAuthDB();
        ticketDB = globalVars.getTicketDB();
    }
    
    @Override
    public void run() {
        running = true;
        LOGGER.log(Level.INFO, "Thread [{0}] running", TicketEngine.class.getSimpleName());
        
        
        while(running) {
            
            // CLEAN OLD TICKETS & AUTHENTICATORS ==============================
            ticketDB.removeExpired();
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
            
            
            // CLEAN OLD TICKETS & AUTHENTICATORS (END)=========================
            
            // Sleep 1ms to prevent synchronization errors it's possible to remove with other code :)
            try {
                sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        LOGGER.log(Level.WARNING, "Thread [{0}] dying", TicketEngine.class.getSimpleName());        
    }
    
    public void ShutDown() {
        this.running = false;
    }
    
    public synchronized String generateAuthenticator(InetAddress address) {
        String authenticator = UnitConversion.ByteArray2Hex(randomizer.generate16bytes());
        // with DB
        String expireTime = ticketDB.newAuthenticator(address, authenticator);
        
        SimpleDateFormat fDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long expire;
        try {
            expire = fDate.parse(expireTime).getTime();
        } catch (ParseException ex) {
            expire = (long) 0;
            Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject json = new JSONObject();
        json.put(JSON_AUTHENTICATOR, authenticator);
        json.put(JSON_TIME_TO_EXPIRE, expire - (new Date()).getTime());
        return json.toString();
    }
  
    public synchronized String checkUserPass(InetAddress address, String userName, String cryptedPass) {
        
        String decodedPass = authDB.getPass4User(userName);
        if(decodedPass == null) {
            System.out.println("No DB data for user " + userName);
            return null;
        }
        if (ticketDB.checkPass(address, userName, decodedPass, cryptedPass))
            return decodedPass;
        /*
        try {
            ResultSet result = ticketDB.getAuthenticatorsByAddress(address);
            int id = -1;
            if (result != null) {
                while (result.next()) {
                    if (Cryptonizer.encryptCoAP(AUTHENTICATION_SECRET_KEY, result.getString("authenticator"), decodedPass).equals(cryptedPass)) {
                        if (ticketDB.checkIfExist(userName)) {
                            //System.out.println("The user is already on the system! don't add it!");
                            return decodedPass;
                        }
                        ticketDB.setUserNameByID(result.getInt("id"), userName);
                        return decodedPass;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        return null;
        
    }
    
    public synchronized String createTicket4User(InetAddress address, String userName, long expireTime, String userType, String userInfo, String userProtocol) {
        
        User user = ticketDB.getUserByName(userName);
        
        if (!address.equals(user.getAddress()))
            System.out.println("Different IP address!!");
        
        if (user != null) {
            JSONObject json = new JSONObject();
            if (user.getUserType() == null || !user.isActive()) {
                String expire = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime));
                String ticket = UnitConversion.ByteArray2Hex(randomizer.generate8bytes());
                userInfo = String.format(" <b>IP:</b> %s <br> <b>Supported Protocols:</b> %s <br> <b>Valid Time:</b> %s <br> <b>Info:</b> %s", address.toString().split("/")[1], userProtocol, expire , userInfo);
                ticketDB.setAllData(user.getId(), address.toString().split("/")[1], userType, userInfo, ticket , expire);
                //updates for web interface
                ticketDB.webAddUser(userName, userInfo, userType);
                json.put(JSON_TICKET, ticket);
                json.put(JSON_TIME_TO_EXPIRE, expireTime - (new Date()).getTime()); // Recalculate to solve synchronization issues
            } else {
                json.put(JSON_TICKET, UnitConversion.ByteArray2Hex(user.getTicket()));
                json.put(JSON_TIME_TO_EXPIRE, user.getExpireTime() - (new Date()).getTime()); // Recalculate to solve synchronization issues
            }
            return json.toString();
        } else
            System.out.println("user NULL");
        return null;
        
        /*
        try {
            ResultSet result = ticketDB.getAllUsersByAddressAndName(address, userName);
            if (result != null) {
                result.next();
                if (result.isLast()) {
                    JSONObject json = new JSONObject();
                    if (result.getString("ticket") == null || result.getBoolean("active") == false) {
                        String expire = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime));
                        String ticket = UnitConversion.ByteArray2Hex(randomizer.generate8bytes());
                        userInfo = String.format(" <b>IP:</b> %s <br> <b>Supported Protocols:</b> %s <br> <b>Valid Time:</b> %s <br> <b>Info:</b> %s", address.toString().split("/")[1], userProtocol, expire , userInfo);
                        ticketDB.setAllData(result.getInt("id"), userType, userInfo, ticket , expire);
                        //updates for web interface
                        ticketDB.webAddUser(userName, userInfo, userType);
                        json.put(JSON_TICKET, ticket);
                        json.put(JSON_TIME_TO_EXPIRE, expireTime - (new Date()).getTime()); // Recalculate to solve synchronization issues
                    } else {
                        try {
                            expireTime = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(result.getString("expire_time")).getTime();
                        } catch (ParseException ex) {
                            expireTime = 0; //repeat the proccess
                            Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        json.put(JSON_TICKET, result.getString("ticket"));
                        json.put(JSON_TIME_TO_EXPIRE, expireTime - (new Date()).getTime()); // Recalculate to solve synchronization issues
                    }
                    return json.toString();
                } else {
                    System.out.println("No UserData on the server (something is working wrong)");
                    return null;
                }
            }
            System.out.println("Null UserData response");
        } catch (SQLException ex) {
            Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
                
        
        return null; */
    }
    
}
