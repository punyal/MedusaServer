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

import com.punyal.medusaserver.protocols.*;
import static org.eclipse.californium.core.coap.CoAP.*;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class EventHandler extends Thread {
    private final EventMessage globalEvent;
    private RADIUS radiusClient;
    private CoAP coapServer;
    
    
    /**
     * Constructor to set the dispatcher
     */
    public EventHandler() {
        this.globalEvent = new EventMessage() {
            @Override
            public void fireEvent(EventMedusa evt) {
                // Do all magic here
                System.out.println("(" + evt.getPriority() + " | " + evt.getType() + ") "
                        +"[" + evt.getTitle() + " : " + evt.getMessage() + "]");
//========================== MOVE THIS TO THE DISPATCHER =======================
if( evt.getProtocol().equals(EventConstants.Protocol.CoAP) && ((CoapExchange)evt.getSource()).getRequestCode().equals(Code.PUT)) {
    
    
        RadiusAuthenticationThread rat = new RadiusAuthenticationThread(EventConstants.Protocol.CoAP, evt.getSource(), "mulle", "mulle");
        rat.start();
    
    
    
    
    //((CoapExchange)evt.getSource()).respond("delayed respond");
}
//==============================================================================
            }
        };
    }

    @Override
    public void run() {
        System.out.println("Event Handler running...");
        // Dispatch here the events
        
        
        
    }
    
    /**
     * Get Event Listener
     * @return a listener
     */
    public EventMessage getEventListener() {
        return globalEvent;
    }
    
    /**
     * Set Protocol Adaptor
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