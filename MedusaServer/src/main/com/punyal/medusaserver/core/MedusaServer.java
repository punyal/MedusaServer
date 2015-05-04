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

import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.protocols.coap.CoAP;
import com.punyal.medusaserver.protocols.rest.REST;
import static java.lang.Thread.sleep;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MedusaServer {
    private static final Logger LOGGER = Logger.getLogger(MedusaServer.class.getCanonicalName());
    private GlobalVars globalVars;
    private CoAP coapServer;     // CoAP Server
    private REST restServer;
    
    /**
     * Constructor
     */
    public MedusaServer() {
        Logger.getLogger(MedusaServer.class.getCanonicalName()).setLevel(Level.ALL);
        
        LOGGER.log(Level.INFO, String.format("Medusa Server %d.%d", version, subVersion));
        
        globalVars = new GlobalVars();
        
        // Start Protocols and Report Status
        try {
            coapServer = new CoAP(globalVars);
            
        } catch (SocketException | IllegalStateException e) {
            System.err.println("Failed to initialize server: " + e.getMessage());
            globalVars.getStatus().setProtocolStatus("CoAP", false);
        }
        
        restServer = new REST(globalVars);
        
        
        try {
            sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(MedusaServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.print(globalVars.getStatus().toString());
        
        
    }
    private void ShutDown() {
        System.out.println("Shutting down Medusa Server"); // Print Reason of ShuttingDown
        coapServer.stop();
        System.exit(0);
    }
    
}