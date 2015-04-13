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

import com.punyal.jrad.JRaDclient;
import static com.punyal.jrad.core.radius.RADIUS.DEFAULT_RADIUS_PORT;
import com.punyal.medusaserver.core.eventHandler.EventHandler;
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.utils.ClassParser;

public class MedusaServer {
    EventHandler evtHandler; // Independent thread for event management
    Status status;     // Status of the Server
    JRaDclient jRaDclient;
    
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
        jRaDclient = new JRaDclient();
        jRaDclient.addListener(ClassParser.eventMedusa2jRAD(evtHandler.getEventListener()));
        
        
        jRaDclient.setSecretKey("RADIUSoffice");
        jRaDclient.setServer("192.168.0.111", DEFAULT_RADIUS_PORT);
        jRaDclient.authenticate("mulle", "mulle");
        
    }
    
}