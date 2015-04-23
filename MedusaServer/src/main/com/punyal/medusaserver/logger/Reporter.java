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
package com.punyal.medusaserver.logger;

import com.punyal.medusaserver.core.security.Ticket;
import com.punyal.medusaserver.utils.UnitConversion;
import java.util.ArrayList;
import java.util.Date;

public class Reporter extends Thread{
    private final ArrayList<Ticket> ticketList;
    private boolean running;
    
    public Reporter(ArrayList<Ticket> ticketList){
        this.ticketList = ticketList;
        running = false;
    }
    
    @Override
    public void run() {
        running = true;
        
        while(running) {
            System.out.println("\n\n-------- Tickets ("+ UnitConversion.Timestamp2String((new Date()).getTime()) +") --------");
            
            ticketList.stream().forEach((ticket) -> {
                if(ticket.getTicket() != null)
                    System.out.println( "| "+
                            ticket.getUserName() + " [" +
                            UnitConversion.ByteArray2Hex(ticket.getTicket()) +"] @ " +
                            ticket.getAddress() + " valid till " +
                            UnitConversion.Timestamp2String(ticket.getExpireTime())
                    );
            });
            
            System.out.println("-----------------------------------------------");
            try {
                sleep(2000); // 2 s
            } catch (InterruptedException ex) {
                
            }
        }
    }
    
}