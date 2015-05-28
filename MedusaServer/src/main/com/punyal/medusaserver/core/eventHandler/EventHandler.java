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
package com.punyal.medusaserver.core.eventHandler;

import com.punyal.medusaserver.core.GlobalVars;
import com.punyal.medusaserver.protocols.coap.CoAPDispatcher;
import com.punyal.medusaserver.protocols.coap.CoAP;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.protocols.*;
import com.punyal.medusaserver.utils.Packetizer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.server.resources.CoapExchange;


/**
 * EventHandler
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class EventHandler extends Thread {
    private static final Logger LOGGER = Logger.getLogger(EventHandler.class.getCanonicalName());
    private boolean running;
    private final EventMessage globalEvent;
    private final TicketEngine ticketEngine;
    private final Status status;
    private List<EventMedusa> messageQueue;
    private RADIUS radiusClient;
    private CoAP coapServer;
    
    public EventHandler(GlobalVars globalVars) {
        running = false;
        status = globalVars.getStatus();
        ticketEngine = globalVars.getTicketEngine();
        this.globalEvent = new EventMessage() {
            @Override
            public void fireEvent(EventMedusa evt) {
                synchronized(EventHandler.this){
                    try {
                        messageQueue.add(evt);
                    }catch(NullPointerException e) {
                        LOGGER.log(Level.WARNING, "Exception {0}", e);
                    }
                    
                }
            }
        };
    }
    
    @Override
    public void run() {
        running = true;
        Logger.getLogger(EventHandler.class.getCanonicalName()).setLevel(Level.ALL);
        LOGGER.log(Level.INFO, "Thread [{0}] running", EventHandler.class.getSimpleName());
        ticketEngine.start();
        messageQueue = new ArrayList<>();
        
        while(running) {
            // DISPATCHER ======================================================
            if(!messageQueue.isEmpty()) {
                EventMedusa evt = messageQueue.remove(0);
                switch(evt.getProtocol()) {
                    case RADIUS:
                        RADIUSDispatcher.dispatchResponse((Packetizer)evt.getSource(), ticketEngine);
                        break;
                    case CoAP:
                        CoAPDispatcher.dispatchRequest((CoapExchange)evt.getSource(), ticketEngine, globalEvent);
                        break;
                    case REST:
                        System.out.println("REST");
                        break;
                    default: throw new IllegalArgumentException("Unknown Protocol " + evt.getProtocol()); 
                }
                
            }
            // DISPATCHER (END) ================================================ 
            
            // Sleep 1ms to prevent synchronization errors it's possible to remove with other code :)
            try {
                sleep(1);
            } catch (InterruptedException ex) {
                Logger.getLogger(EventHandler.class.getName()).log(Level.SEVERE, null, ex);
            }   
        }
        LOGGER.log(Level.WARNING, "Thread [{0}] dying", EventHandler.class.getSimpleName());
    }
    
    /**
     * Method to ShutDown the Handler Engine
     */
    public void ShutDown() {
        this.running = false;
        ticketEngine.ShutDown();
    }
    
    /**
     * Event Listener Getter
     * @return a listener
     */
    public EventMessage getEventListener() {
        return globalEvent;
    }
    
    /**
     * Protocol Adaptor Setter
     * @param adaptor of each protocol
     */
    public void setProtocolAdaptor(Object adaptor) {
        switch(adaptor.getClass().getSimpleName()) {
            case "CoAP":
                this.coapServer = (CoAP)adaptor;
                break;
            case "RADIUS":
                this.radiusClient = (RADIUS)adaptor;
                break;
            default: throw new IllegalArgumentException("Unknown Adaptor "+adaptor.getClass().getSimpleName()); 
        }
    }
}