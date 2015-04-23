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
package com.punyal.medusaserver.protocols.coap;

import com.punyal.medusaserver.core.eventHandler.EventConstants;
import static com.punyal.medusaserver.core.eventHandler.EventConstants.Priority.NORMAL;
import com.punyal.medusaserver.core.eventHandler.EventMedusa;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import com.punyal.medusaserver.core.eventHandler.EventSource;
import com.punyal.medusaserver.core.security.Ticket;
import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.utils.UnitConversion;
import java.net.SocketException;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoAP extends CoapServer {
    public static EventConstants.Priority PRIORITY = NORMAL;
    
    private EventSource mlistener = new EventSource();
    
    private TicketEngine ticketEngine;
    
    public void addListener(EventMessage listener) {
        mlistener.addEventListener(listener);
    }
    
    
    
    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public CoAP(TicketEngine ticketEngine) throws SocketException {
        this.ticketEngine = ticketEngine;
        // provide an instance of a Authentication resource
        add(new CoAP_Authentication_Resource());
        add(new CoAP_Validation_Resource());
    }
    
    /*
     * Definition of the Authentication Resource
     */
    class CoAP_Authentication_Resource extends MedusaCoapResource {
        
        public CoAP_Authentication_Resource() {
            
            // set resource identifier
            super(ticketEngine, "Authentication",true);
            
            // set display name
            getAttributes().setTitle("Authentication Resource");
        }
        
        @Override
        public void handleGET(CoapExchange exchange) {
            EventMedusa newEvt;
            
            newEvt = new EventMedusa(CoAP.PRIORITY,
                    EventConstants.Protocol.CoAP,
                    EventConstants.Type.NORMAL,
                    "CoAP_Authentication GET :: " + exchange.getRequestText(),
                    "CoAP Server",
                    exchange);
            
            mlistener.newEvent(newEvt);
        }
        
        @Override
        public void handlePUT(CoapExchange exchange) {
            EventMedusa newEvt;
            
            newEvt = new EventMedusa(CoAP.PRIORITY,
                    EventConstants.Protocol.CoAP,
                    EventConstants.Type.NORMAL,
                    "CoAP_Authentication PUT :: " + exchange.getRequestText(),
                    "CoAP Server",
                    exchange);
            
            mlistener.newEvent(newEvt);
        }
    }
    
    
    /*
     * Definition of the Validation Resource
     */
    class CoAP_Validation_Resource extends MedusaCoapResource {
        
        public CoAP_Validation_Resource() {
            
            // set resource identifier
            super(ticketEngine, "Validation",true);
            
            // set display name
            getAttributes().setTitle("Validation Resource");
        }
                
        @Override
        public void medusaHandlePUT(CoapExchange exchange) {
            try {
                System.out.println(exchange.getRequestText());
                Ticket ticket = ticketEngine.getTicket(exchange.getRequestText());
                if(ticket != null) {
                    exchange.respond("Valid Ticket");
                }
            } catch(NullPointerException e) {
                exchange.respond("Invalid Ticket");
            }
        }
    }
}
