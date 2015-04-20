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

import com.punyal.medusaserver.core.db.Query;
import static com.punyal.medusaserver.core.medusa.Configuration.GENERIC_TICKET_TIMEOUT;
import com.punyal.medusaserver.utils.UnitConversion;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;

public class TicketEngine extends Thread{
    private static final Logger LOGGER = Logger.getLogger(TicketEngine.class.getCanonicalName());
    private boolean running;
    private final Randomizer randomizer;
    private ArrayList<Ticket> ticketList;
    
    public TicketEngine() {
        running = false;
        randomizer = new Randomizer();
        ticketList = new ArrayList<>();
    }
    
    public void run() {
        running = true;
        LOGGER.log(Level.INFO, "Thread [{0}] running", TicketEngine.class.getSimpleName());
        
        while(running) {
            
            // CLEAN OLD TICKETS & AUTHENTICATORS ==============================
            long actualTime = (new Date()).getTime();
            while((!this.getTicketList().isEmpty())  && (this.getTicketList().get(0).getExpireTime() < actualTime)) {
                Ticket tmp = this.getTicketList().remove(0);
                if(tmp.getTicket() == null)
                    System.err.println("Authenticator EXPIRED ["+ UnitConversion.ByteArray2Hex(tmp.getAuthenticator())
                            +"] @ "+tmp.getAddress());
                else
                    System.err.println("Ticket EXPIRED ["+UnitConversion.ByteArray2Hex(tmp.getTicket())
                            +"] for user \""+tmp.getUserName()+"\" @ "+tmp.getAddress());
            }
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
    
    public String generateAuthenticator(InetAddress address) {
        Ticket ticket = new Ticket(address, randomizer.generate16bytes());
        ticketList.add(ticket);
        Collections.sort(ticketList);
        JSONObject json = new JSONObject();
        json.put("Authenticator", UnitConversion.ByteArray2Hex(ticket.getAuthenticator()));
        json.put("ExpireTime", ticket.getExpireTime());
        return json.toString();
    }
    
    public void printList() {
        System.out.println("-------- Tickets --------");
        
        ticketList.stream().forEach((ticket) -> {
            System.out.println("IP[" + ticket.getAddress() +
                    "] UserName[" + ticket.getUserName() +
                    "] UserPass[" + ticket.getUserPass()+
                    "] Ticket[" + UnitConversion.ByteArray2Hex(ticket.getTicket())+
                    "] Authenticator[" + UnitConversion.ByteArray2Hex(ticket.getAuthenticator())+
                    "] ExpireTime[" + UnitConversion.Timestamp2String(ticket.getExpireTime())+
                    "]" );
        });
        
        System.out.println("-------------------------");
    }
    
    public boolean checkUserPass(Query dbQuery, InetAddress address, String userName, String cryptedPass) {
        
        return true;
    }
    
    public ArrayList<Ticket> getTicketList() {
        return ticketList;
    }
    
    public String createTicket4User(Query dbQuery, InetAddress address, String userName, long expireTime) {
        if(dbQuery.getPass4User(userName) == null)
            return null;
        if(ticketList.isEmpty())
            return null;
        
        // TODO: finish this correctly
        Ticket ticket = ticketList.get(0);
        ticket.setUserName(userName);
        ticket.setTicket(randomizer.generate8bytes());
        if(expireTime == 0)
            ticket.setExpireTime((new Date()).getTime() + (GENERIC_TICKET_TIMEOUT));
        else
            ticket.setExpireTime(expireTime);
        
        Collections.sort(ticketList);
        JSONObject json = new JSONObject();
        json.put("Ticket", UnitConversion.ByteArray2Hex(ticket.getTicket()));
        json.put("ExpireTime", ticket.getExpireTime());
        return json.toString();
    }
}