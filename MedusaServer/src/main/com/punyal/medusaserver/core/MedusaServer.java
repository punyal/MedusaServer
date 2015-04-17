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

import com.punyal.medusaserver.core.eventHandler.EventHandler;
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.protocols.CoAP;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.config.NetworkConfig;

public class MedusaServer {
    private static final Logger LOGGER = Logger.getLogger(MedusaServer.class.getCanonicalName());
    private EventHandler evtHandler; // Independent thread for event management
    private Status status;     // Status of the Server
    //private RADIUS radiusClient; // RADIUS Client
    private CoAP coapServer;     // CoAP Server
    
    /**
     * Constructor
     */
    public MedusaServer() {
        Logger.getLogger(MedusaServer.class.getCanonicalName()).setLevel(Level.ALL);
        
        LOGGER.log(Level.INFO, String.format("Medusa Server %d.%d", version, subVersion));
        
        // Create and set the server status
        status = new Status();
        
        // Create and Start the Event Handler
        evtHandler = new EventHandler();
        evtHandler.start();
        
        // Start Protocols and Report Status
        try {
            coapServer = new CoAP();
            // Set logger
            Logger.getLogger(CoapServer.class.getCanonicalName()).setLevel(Level.OFF);
            Logger.getLogger("org.eclipse.californium.core.network.CoAPEndpoint").setLevel(Level.OFF);
            
            coapServer.addListener(evtHandler.getEventListener());
            evtHandler.setProtocolAdaptor(coapServer);
            coapServer.start();
            
        } catch (SocketException e) {
            
            System.err.println("Failed to initialize server: " + e.getMessage());
        }
        
        // Now the system is ready
        // Evaluate Server Status and Make a decision.
        // sleep and evaluate some secs later.
        // ShutDown();
    }
    private void ShutDown() {
        System.out.println("Shutting down Medusa Server"); // Print Reason of ShuttingDown
        coapServer.stop();
        System.exit(0);
    }
    
}