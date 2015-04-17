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
import java.util.List;

public class TicketEngine {
    private final Randomizer randomizer;
    private ArrayList<Ticket> ticketList;
    
    public TicketEngine() {
        randomizer = new Randomizer();
        ticketList = new ArrayList<>();
    }
    
    public byte[] generateAuthenticator(InetAddress address) {
        Ticket ticket = new Ticket(address, randomizer.generate16bytes());
        ticketList.add(ticket);
        Collections.sort(ticketList);
        return ticket.getAuthenticator();
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
    
    public Ticket createTicket4User(Query dbQuery, InetAddress address, String userName, long expireTime) {
        if(dbQuery.getPass4User(userName) == null)
            return null;
        if(ticketList.isEmpty())
            return null;
        
        // TODO: finish this correctly
        Ticket tmpTicket = ticketList.get(0);
        tmpTicket.setUserName(userName);
        tmpTicket.setTicket(randomizer.generate8bytes());
        if(expireTime == 0)
            tmpTicket.setExpireTime((new Date()).getTime() + (GENERIC_TICKET_TIMEOUT));
        else
            tmpTicket.setExpireTime(expireTime);
        
        return ticketList.get(0);
    }
}