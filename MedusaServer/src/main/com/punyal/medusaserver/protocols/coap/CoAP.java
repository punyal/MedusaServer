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
import static com.punyal.medusaserver.core.medusa.MedusaConstants.*;
import com.punyal.medusaserver.core.security.User;
import java.net.SocketException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * CoAP
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class CoAP extends CoapServer {
    public static EventConstants.Priority PRIORITY = NORMAL;
    private final EventSource mlistener = new EventSource();
    private final GlobalVars globalVars;
    
    /**
     * Add new Listener
     * @param listener to add 
     */
    public void addListener(EventMessage listener) {
        mlistener.addEventListener(listener);
    }
    
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
    
    /**
     * Authentication Resource
     */
    class CoAP_Authentication_Resource extends MedusaCoapResource {
        
        public CoAP_Authentication_Resource() {
            super(globalVars, "Authentication",true);
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
     * Validation Resource
     */
    class CoAP_Validation_Resource extends MedusaCoapResource {
        
        public CoAP_Validation_Resource() {
            super(globalVars, "Validation",true);
            getAttributes().setTitle("Validation Resource");
        }
                
        @Override
        public void medusaHandlePUT(CoapExchange exchange) {
            
            JSONObject json = (JSONObject) JSONValue.parse(exchange.getRequestText());
            String providerTicket = json.get(JSON_MY_TICKET).toString();
            String consumerTicket = json.get(JSON_TICKET).toString();
            User provider = globalVars.getTicketDB().getUserByTicket(providerTicket);
            User consumer = globalVars.getTicketDB().getUserByTicket(consumerTicket);
            if (provider == null) {
                exchange.respond("Invalid Ticket");
                return;
            }
            if (consumer == null) {
                exchange.respond("Invalid Ticket");
                return;
            }
            json.clear();
            try {
                json.put(JSON_TIME_TO_EXPIRE, consumer.getExpireTime()- (new Date()).getTime());
                json.put(JSON_USER_NAME, consumer.getUserName());
                json.put(JSON_ADDRESS, consumer.getAddress().toString());
            } catch(NullPointerException e) {
                System.err.println("Null pointer info: "+e);
            }
            exchange.respond(json.toString());
            // updates for web interface
            globalVars.getTicketDB().webAddLinkFrom(consumer.getUserName(), provider.getUserName());
        }
    }
}
