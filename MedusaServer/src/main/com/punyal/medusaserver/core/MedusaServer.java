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
package com.punyal.medusaserver.core;

import com.punyal.medusaserver.core.db.DBsql;
import com.punyal.medusaserver.core.eventHandler.EventHandler;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.protocols.CoAP;
import java.net.SocketException;

public class MedusaServer {
    EventHandler evtHandler; // Independent thread for event management
    Status status;     // Status of the Server
    //RADIUS radiusClient; // RADIUS Client
    CoAP coapServer;     // CoAP Server
    
    /**
     * Constructor
     */
    public MedusaServer() {
        System.out.println("Medusa Server " +
                String.format("%d.%d", version, subVersion));
        
        // Create and set the server status
        status = new Status();
        
        // Create and Start the Event Handler
        evtHandler = new EventHandler();
        evtHandler.start();
        
        // Create and Start a RADIUS client
        //radiusClient = new RADIUS();
        //radiusClient.addListener(ClassParser.eventMedusa2jRAD(evtHandler.getEventListener()));
        //radiusClient.setSecretKey("RADIUSoffice");
        //radiusClient.setServer("192.168.0.111", RADIUS.DEFAULT_PORT);
        //evtHandler.setProtocolAdaptor(radiusClient);
        
        // Create and Start a CoAP Server
        try {
            coapServer = new CoAP();
            coapServer.addListener(evtHandler.getEventListener());
            evtHandler.setProtocolAdaptor(coapServer);
            coapServer.start();
            
        } catch (SocketException e) {
            
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
        
        
        // Test a users
        
        //radiusClient.authenticate("mulle215", "mulle215");
        //radiusClient.authenticate("mulle", "mulle");
        
        // Test DB
        DBsql db = new DBsql(MySQL_USER, MySQL_USER_PASSWORD, MySQL_SERVER);
        db.Query("SELECT * FROM radcheck");
        
    }
    
}