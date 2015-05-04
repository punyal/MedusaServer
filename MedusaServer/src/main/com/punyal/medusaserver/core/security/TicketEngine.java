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

import com.punyal.medusaserver.core.db.AuthenticationDB;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
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
    
    @Override
    public void run() {
        running = true;
        LOGGER.log(Level.INFO, "Thread [{0}] running", TicketEngine.class.getSimpleName());
        
        while(running) {
            
            // CLEAN OLD TICKETS & AUTHENTICATORS ==============================
            long actualTime = (new Date()).getTime();
            while((!ticketList.isEmpty())  && (ticketList.get(0).getExpireTime() < actualTime)) {
                Ticket tmp = ticketList.remove(0);
                /*
                if(tmp.getTicket() == null)
                    System.err.println("Authenticator EXPIRED ["+ tmp.getAuthenticator()
                            +"] @ "+tmp.getAddress());
                else
                    System.err.println("Ticket EXPIRED ["+UnitConversion.ByteArray2Hex(tmp.getTicket())
                            +"] for user \""+tmp.getUserName()+"\" @ "+tmp.getAddress());
                */
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
    
    public synchronized String generateAuthenticator(InetAddress address) {
        Ticket ticket = new Ticket(address, UnitConversion.ByteArray2Hex(randomizer.generate16bytes()));
        ticketList.add(ticket);
        Collections.sort(ticketList);
        JSONObject json = new JSONObject();
        json.put(JSON_AUTHENTICATOR, ticket.getAuthenticator());
        json.put(JSON_TIME_TO_EXPIRE, ticket.getExpireTime() - (new Date()).getTime());
        return json.toString();
    }
    
    private synchronized void removeByTicket(Ticket ticket) {
        ticketList.remove(ticket);
    }
    
    private synchronized void removeByAuthenticator(String authenticator) {
        ticketList.stream().forEach((ticket) -> {
            if( ticket.getAuthenticator().equals(authenticator) )
                ticketList.remove(ticket);
        });
    }
    
    private synchronized void removeByUser(String userName) {
        ticketList.stream().forEach((ticket) -> {
            if( ticket.getUserName().equals(userName) )
                ticketList.remove(ticket);
        });
    }
    
    private synchronized Ticket findByUser(String userName) {
        int i=0;
        while(ticketList.size() > i) {
            if(ticketList.get(i).getUserName().equals(userName))
                return ticketList.get(i);
            i++;
        }
        return null;
    }
    
    private ArrayList<Ticket> getPossibleAuthenticationTicketsByAddress(InetAddress address) {
        ArrayList<Ticket> possibleTickets = new ArrayList<>();
        int i=0;
        while(ticketList.size() > i) {
            if(ticketList.get(i).getAddress().equals(address))
                possibleTickets.add(ticketList.get(i));
            i++;
        }
        return possibleTickets;
    }
    
    private synchronized ArrayList<Ticket> getPossibleTicketsByAddress(InetAddress address, String userName) {
        ArrayList<Ticket> possibleTickets = new ArrayList<>();
        int i=0;
        while(ticketList.size() > i) {
            try {
                if(ticketList.get(i).getAddress().equals(address) && ticketList.get(i).getUserName().equals(userName))
                    possibleTickets.add(ticketList.get(i));
            } catch(NullPointerException e) {
                LOGGER.log(Level.WARNING, "Get Possible Tickets By Address exception " + e);
            }
            i++;
        }
        return possibleTickets;
    }
    
    public synchronized String checkUserPass(AuthenticationDB authDB, InetAddress address, String userName, String cryptedPass) {
        ArrayList<Ticket> possibleList = getPossibleAuthenticationTicketsByAddress(address);
        if(possibleList.isEmpty()) {
            System.out.println("Possible List Empty!");
            return null;
        }
        
        String decodedPass = authDB.getPass4User(userName);
        if(decodedPass == null) {
            System.out.println("No DB data for user " + userName);
            return null;
        }
        
        int i=0;
        String validPass = null;
        while(possibleList.size() > i) {
            validPass = Cryptonizer.encryptCoAP(AUTHENTICATION_SECRET_KEY, possibleList.get(i).getAuthenticator(), decodedPass);
            //System.out.println(validPass +" vs "+cryptedPass);
            if(validPass.equals(cryptedPass))
                break;
            i++;
        }
        
        if(possibleList.size() == i) {
            System.out.println("No valid passwords on the list");
            return null;
        }
        
        if(validPass == null) {
            System.out.println("No valid Password!");
            return null;
        }
        
        if(validPass.equals(cryptedPass)) {
            possibleList.get(i).setUserName(userName);
            possibleList.get(i).setUserPass(decodedPass);
            return decodedPass;
        }
        
        return null;
    }
    
    public synchronized String createTicket4User(AuthenticationDB authDB, InetAddress address, String userName, long expireTime, String userType) {
        Ticket ticket;
        ArrayList<Ticket> possibleList = getPossibleTicketsByAddress(address, userName);
        if(possibleList.isEmpty()) {
            System.out.println("Possible List Empty!");
            return null;
        }
        // TODO: Check This on the Future
        if(possibleList.size() > 1) {
            System.out.println("More than one same-user");
            ticket = possibleList.get(possibleList.size()-1);
        } else {
            ticket = possibleList.get(0);
        }
        
        if(ticket.getTicket() == null) {
            ticket.setUserName(userName);
            ticket.setUserType(userType);
            ticket.setTicket(randomizer.generate8bytes());
            ticket.setExpireTime(expireTime);
            Collections.sort(ticketList);
        }
        
        JSONObject json = new JSONObject();
        json.put(JSON_TICKET, UnitConversion.ByteArray2Hex(ticket.getTicket()));
        json.put(JSON_TIME_TO_EXPIRE, ticket.getExpireTime() - (new Date()).getTime()); // Recalculate to solve synchronization issues
        return json.toString();
    }
    
    public synchronized Ticket getTicket(String ticket) {
        int i=0;
        while(ticketList.size() > i) {
            try {
                if(UnitConversion.ByteArray2Hex(ticketList.get(i).getTicket()).equals(ticket))
                    return ticketList.get(i);
                    
            } catch(NullPointerException e) {
                LOGGER.log(Level.WARNING, "Get Possible Tickets By Address exception {0}", e);
                return null;
            }
            i++;
        }
        return null;
    }
    
    public synchronized ArrayList<Ticket> getTicketList() {
        return ticketList;
    }
    
    public synchronized void printList(ArrayList<Ticket> list) {
        System.out.println("-------- Tickets --------");
        
        list.stream().forEach((ticket) -> {
            System.out.println("IP[" + ticket.getAddress() +
                    "] UserName[" + ticket.getUserName() +
                    "] UserPass[" + ticket.getUserPass()+
                    "] Ticket[" + UnitConversion.ByteArray2Hex(ticket.getTicket())+
                    "] Authenticator[" + ticket.getAuthenticator()+
                    "] ExpireTime[" + UnitConversion.Timestamp2String(ticket.getExpireTime())+
                    "]" );
        });
        
        System.out.println("-------------------------");
    }
}
