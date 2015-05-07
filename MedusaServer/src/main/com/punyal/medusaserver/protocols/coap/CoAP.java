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

import com.punyal.medusaserver.core.GlobalVars;
import com.punyal.medusaserver.core.eventHandler.EventConstants;
import static com.punyal.medusaserver.core.eventHandler.EventConstants.Priority.NORMAL;
import com.punyal.medusaserver.core.eventHandler.EventMedusa;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import com.punyal.medusaserver.core.eventHandler.EventSource;
import com.punyal.medusaserver.core.medusa.Configuration;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import com.punyal.medusaserver.core.security.Ticket;
import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.utils.UnitConversion;
import java.net.SocketException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CoAP extends CoapServer {
    public static EventConstants.Priority PRIORITY = NORMAL;
    
    private EventSource mlistener = new EventSource();
    
    private GlobalVars globalVars;
    
    public void addListener(EventMessage listener) {
        mlistener.addEventListener(listener);
    }
    
    
    
    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public CoAP(GlobalVars globalVars) throws SocketException {
        // provide an instance of a Authentication resource
        this.globalVars = globalVars;
        add(new CoAP_Authentication_Resource());
        add(new CoAP_Validation_Resource());
        Logger.getLogger(CoapServer.class.getCanonicalName()).setLevel(Level.OFF);
        Logger.getLogger("org.eclipse.californium.core.network.CoAPEndpoint").setLevel(Level.OFF);
        Logger.getLogger("org.eclipse.californium.core.network.Matcher").setLevel(Level.OFF);
        
        
        addListener(this.globalVars.getHandler().getEventListener());
        this.globalVars.getHandler().setProtocolAdaptor(this);
        this.globalVars.getStatus().addNewProtocolStatus(this.getClass().getSimpleName());
        start();
    }
    
    /*
     * Definition of the Authentication Resource
     */
    class CoAP_Authentication_Resource extends MedusaCoapResource {
        
        public CoAP_Authentication_Resource() {
            
            // set resource identifier
            super(globalVars, "Authentication",true);
            
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
            super(globalVars, "Validation",true);
            
            // set display name
            getAttributes().setTitle("Validation Resource");
        }
                
        @Override
        public void medusaHandlePUT(CoapExchange exchange) {
            
            JSONObject json = (JSONObject) JSONValue.parse(exchange.getRequestText());
            String providerTicket = json.get(JSON_MY_TICKET).toString();
            String consumerTicket = json.get(JSON_TICKET).toString();
            
            Ticket ticketProvider = globalVars.getTicketEngine().getTicket(providerTicket);
            Ticket ticketConsumer = globalVars.getTicketEngine().getTicket(consumerTicket);
            
            if (ticketProvider == null) {
                //System.out.println("Invalid Ticket Provider");
                exchange.respond("Invalid Ticket");
                return;
            }
            
            if (ticketConsumer == null) {
                //System.out.println("Invalid Ticket Consumer");
                exchange.respond("Invalid Ticket");
                return;
            }
            
            //System.out.println("Validation OK!");
            json.clear();
            try {
                json.put(JSON_TIME_TO_EXPIRE, ticketConsumer.getExpireTime()- (new Date()).getTime());
                json.put(JSON_USER_NAME, ticketConsumer.getUserName());
                json.put(JSON_ADDRESS, ticketConsumer.getAddress().toString());
            } catch(NullPointerException e) {
                System.err.println("Null pointer info: "+e);
            }
            exchange.respond(json.toString());
            String providerName = ticketProvider.getUserName();
            ticketConsumer.addConnection(providerName);
            globalVars.getNetDB().addLink(ticketConsumer.getUserName(), ticketProvider.getUserName());
            
            
            
        }
    }
}
