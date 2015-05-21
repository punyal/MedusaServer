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

import com.punyal.medusaserver.utils.UnitConversion;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User{
    private final int id;
    private final InetAddress address;
    private final String userName;
    private final String userType;
    private final String userInfo;
    private final String connections;
    private final byte ticket[];
    private final String authenticator;
    private final long expireTime;
    private final boolean active;
            
    public User(int id, InetAddress address, String userName, String userType, String userInfo, String connections, String ticket, String authenticator, String expireTime, boolean active) {
        this.id = id;
        this.address = address;
        this.userName = userName;
        this.userType = userType;
        this.userInfo = userInfo;
        this.connections = connections;
        this.ticket = (ticket!=null)?UnitConversion.hexStringToByteArray(ticket):null;
        this.authenticator = authenticator;
        this.active = active;
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long date;
        try {
            date = dFormat.parse(expireTime).getTime();
        } catch (ParseException ex) {
            date = (long) 0;
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.expireTime = date;
    }
    
    public int getId() {
        return id;
    }
    
    public InetAddress getAddress() {
        return address;
    }
    
    public String getUserName() {
        return userName;
    }
        
    public String getUserType() {
        return userType;
    }
    
    
    public String getUserInfo() {
        return userInfo;
    }
    
    public byte[] getTicket() {
        return ticket;
    }
    
    public String getAuthenticator() {
        return authenticator;
    }
        
    public long getExpireTime() {
        return expireTime;
    }
    
    public boolean isActive() {
        return active;
    }
}