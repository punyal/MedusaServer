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

import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;

public class Ticket  implements Comparable<Ticket>{
    private final InetAddress address;
    private String userName;
    private String userPass;
    private String userType;
    private String userInfo;
    private ArrayList<String> connections;
    private byte ticket[];
    private final String authenticator;
    private long expireTime;
            
    public Ticket(InetAddress address, String authenticator) {
        this.address = address;
        userName = null;
        userPass = null;
        userType = null;
        userInfo = null;
        ticket = null;
        connections = new ArrayList<>();
        expireTime = (new Date()).getTime() + (AUTHENTICATION_CODE_TIMEOUT);
        this.authenticator = authenticator;
    }
    
    public InetAddress getAddress() {
        return address;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    
    public String getUserInfo() {
        return userInfo;
    }
    
    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
    
    public String getUserPass() {
        return userPass;
    }
    
    public void setTicket(byte ticket[]) {
        this.ticket = ticket;
    }
    
    public byte[] getTicket() {
        return ticket;
    }
    
    public String getAuthenticator() {
        return authenticator;
    }
    
    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }
    
    public long getExpireTime() {
        return expireTime;
    }
    
    private int findConnection(String connection) {
        if (connections.size() > 0) {
            for (int i=0; i < connections.size(); i++) {
                if (connections.get(i).equals(connection))
                    return i;
            }
        }
        return -1;
    }
    
    public synchronized void addConnection(String connection) {
        if( findConnection(connection) == -1 )
            connections.add(connection);
    }
    
    public void removeConnection(String connection) {
        int index = findConnection(connection);
        if( index != -1 )
            connections.remove(index);
    }
    
    public ArrayList<String> getConnections() {
        return connections;
    }
    
    public boolean isValid() {
        Date date = new Date();
        return date.getTime() < expireTime;
    }
    
    @Override
    public int compareTo(Ticket t) {
        return (int)(expireTime - t.expireTime);
    }
}