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

import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.utils.UnitConversion;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class MedusaCoapResource extends CoapResource{
    private final boolean publicResource;
    private TicketEngine ticketEngine;
    
    public MedusaCoapResource(TicketEngine ticketEngine, String name, boolean publicResource) {
        super(name);
        this.publicResource = publicResource;
        this.ticketEngine = ticketEngine;
    }
    
    public MedusaCoapResource(TicketEngine ticketEngine, String name, boolean visible, boolean publicResource) {
        super(name, visible);
        this.publicResource = publicResource;
        this.ticketEngine = ticketEngine;
    }
    
    @Override
    public void handleGET(CoapExchange exchange) {
        // PUBLIC RESOURCE
        if(publicResource) {
            medusaHandleGET(exchange);
            return;
        }
        
        // NOT PUBLIC RESOURCE
        try {
            if(ticketEngine.getTicket(UnitConversion.ByteArray2Hex(UnitConversion.getTicketFromCoapExchange(exchange))) != null) {
                medusaHandleGET(exchange);
                return;
            }
        } catch(NullPointerException e) {}
        // Empty response to prevent retransmissions and saturation
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    
    @Override
    public void handlePOST(CoapExchange exchange) {
        // PUBLIC RESOURCE
        if(publicResource) {
            medusaHandlePOST(exchange);
            return;
        }
        // NOT PUBLIC RESOURCE
        try {
            if(ticketEngine.getTicket(UnitConversion.ByteArray2Hex(UnitConversion.getTicketFromCoapExchange(exchange))) != null) {
                medusaHandlePOST(exchange);
                return;
            }
        } catch(NullPointerException e) {}
        // Empty response to prevent retransmissions and saturation
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    
    @Override
    public void handlePUT(CoapExchange exchange) {
        // PUBLIC RESOURCE
        if(publicResource) {
            medusaHandlePUT(exchange);
            return;
        }
        // NOT PUBLIC RESOURCE
        try {
            if(ticketEngine.getTicket(UnitConversion.ByteArray2Hex(UnitConversion.getTicketFromCoapExchange(exchange))) != null) {
                medusaHandlePUT(exchange);
                return;
            }
        } catch(NullPointerException e) {}
        // Empty response to prevent retransmissions and saturation
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    
    @Override
    public void handleDELETE(CoapExchange exchange) {
        // PUBLIC RESOURCE
        if(publicResource) {
            medusaHandleDELETE(exchange);
            return;
        }
        // NOT PUBLIC RESOURCE
        try {
            if(ticketEngine.getTicket(UnitConversion.ByteArray2Hex(UnitConversion.getTicketFromCoapExchange(exchange))) != null) {
                medusaHandleDELETE(exchange);
                return;
            }
        } catch(NullPointerException e) {}
        // Empty response to prevent retransmissions and saturation
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    
    public void medusaHandleGET(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    public void medusaHandlePOST(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    public void medusaHandlePUT(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    public void medusaHandleDELETE(CoapExchange exchange) {
        exchange.respond(CoAP.ResponseCode.METHOD_NOT_ALLOWED);
    }
    
}