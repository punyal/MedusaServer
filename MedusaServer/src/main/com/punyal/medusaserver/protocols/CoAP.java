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
package com.punyal.medusaserver.protocols;

import com.punyal.medusaserver.core.eventHandler.EventConstants;
import static com.punyal.medusaserver.core.eventHandler.EventConstants.Priority.NORMAL;
import com.punyal.medusaserver.core.eventHandler.EventMedusa;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import com.punyal.medusaserver.core.eventHandler.EventSource;
import java.net.SocketException;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoAP extends CoapServer {
    public static EventConstants.Priority PRIORITY = NORMAL;
    
    private EventSource mlistener = new EventSource();
    
    public void addListener(EventMessage listener) {
        mlistener.addEventListener(listener);
    }
    
    
    
    /*
     * Constructor for a new Hello-World server. Here, the resources
     * of the server are initialized.
     */
    public CoAP() throws SocketException {
        
        // provide an instance of a Hello-World resource
        add(new CoAP_Authentication_Resource());
    }
    
    /*
     * Definition of the Hello-World Resource
     */
    class CoAP_Authentication_Resource extends CoapResource {
        
        public CoAP_Authentication_Resource() {
            
            // set resource identifier
            super("CoAP_Authentication");
            
            // set display name
            getAttributes().setTitle("AAA Resource");
        }
        
        @Override
        public void handleGET(CoapExchange exchange) {
            EventMedusa newEvt;
            
            newEvt = new EventMedusa(CoAP.PRIORITY,
                    EventConstants.Protocol.CoAP,
                    EventConstants.Type.NORMAL,
                    "CoAP_Authentication GET",
                    "CoAP Server",
                    exchange);
            
            mlistener.newEvent(newEvt);
            // respond to the request
            exchange.respond("String to encrypt");
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
            // respond to the request
            //exchange.respond("AAA PUT");
        }
    }
}
