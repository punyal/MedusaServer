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
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.utils.UnitConversion;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

/**
 * TicketEngine
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
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
            // Clean old Tickets & Authenticators
            ticketDB.removeExpired();
            // Sleep 100ms before repeat it
            try {
                sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(TicketEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        LOGGER.log(Level.WARNING, "Thread [{0}] dying", TicketEngine.class.getSimpleName());        
    }
    
    /**
     * Method to ShutDown Ticket Engine
     */
    public void ShutDown() {
        this.running = false;
    }
    
    /**
     * Generate Authenticator for a new request by IP address.
     * @param address IP of the new request
     * @return String of the Authenticator
     */
    public synchronized String generateAuthenticator(InetAddress address) {
        String authenticator = UnitConversion.ByteArray2Hex(randomizer.generate16bytes());
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
    
    /**
     * Check if a password is valid or not
     * @param address of the client
     * @param userName of the client
     * @param cryptedPass of the client
     * @return decrypted pass
     */
    public synchronized String checkUserPass(InetAddress address, String userName, String cryptedPass) {
        
        String decryptedPass = authDB.getPass4User(userName);
        if(decryptedPass == null) {
            System.out.println("No DB data for user " + userName);
            return null;
        }
        if (ticketDB.checkPass(address, userName, decryptedPass, cryptedPass))
            return decryptedPass;
        return null;
    }
    
    /**
     * Create Ticket for a specific user
     * @param address of the user
     * @param userName of the user
     * @param expireTime of the user
     * @param userType of the user
     * @param userInfo of the user
     * @param userProtocol of the user
     * @return JSON string of the ticket
     */
    public synchronized String createTicket4User(InetAddress address, String userName, long expireTime, String userType, String userInfo, String userProtocol) {
        User user = ticketDB.getUserByName(userName);
        if (!address.equals(user.getAddress()))
            System.out.println("Different IP address!!");
        if (user != null) {
            JSONObject json = new JSONObject();
            if (user.getUserType() == null || !user.isActive() || (user.getExpireTime() - (new Date()).getTime()) < 0) {
                String expire = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime));
                String ticket = UnitConversion.ByteArray2Hex(randomizer.generate8bytes());
                userInfo = String.format(" <b>IP:</b> %s <br> <b>Supported Protocols:</b> %s <br> <b>Valid Time:</b> %s <br> <b>Info:</b> %s", address.toString().split("/")[1], userProtocol, expire , userInfo);
                ticketDB.setAllData(user.getId(), address.toString().split("/")[1], userType, userInfo, ticket , expire);
                // updates for web interface
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
    }
}
