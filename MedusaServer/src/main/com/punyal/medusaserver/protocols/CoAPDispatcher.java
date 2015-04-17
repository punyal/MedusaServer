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

import com.punyal.jrad.core.radius.Message;
import com.punyal.medusaserver.core.db.Query;
import com.punyal.medusaserver.core.eventHandler.EventConstants;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import com.punyal.medusaserver.core.security.Randomizer;
import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.utils.UnitConversion;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class CoAPDispatcher {
    private CoAPDispatcher() {}
    
    public static void dispatchRequest(CoapExchange coapReq, Randomizer randomizer, Query dbQuery, TicketEngine ticketEngine, EventMessage globalEvent) {
        switch(coapReq.getRequestCode()) {
            case GET: // Request a Authenticator Code
                String newAuthenticator = UnitConversion.ByteArray2Hex(randomizer.generate16bytes());
                // Save the information at this point
                coapReq.respond("Authenticator [" + newAuthenticator + "]");
                break;
            case PUT: // Request a valid Ticket
                String[] userPass = coapReq.getRequestText().split("@");
                if(userPass.length != 2) {
                    coapReq.respond(ResponseCode.NOT_ACCEPTABLE, "Wrong user-password format");
                }else{
                    // Check if the pass is valid
                    System.out.println("Correct PassWord " + dbQuery.getPass4User(userPass[0]));

                    RadiusAuthenticationThread rat = new RadiusAuthenticationThread(
                        EventConstants.Protocol.CoAP,
                        coapReq,
                        userPass[0],
                        userPass[1]);
                    rat.addListener(globalEvent);
                    rat.start();
                }
                break;
            default:
                coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Unvalid CoAP Code");
                break;
        }
    }
    
    public static void dispatchResponse(Message radRequest, CoapExchange coapReq, TicketEngine ticketEngine) {
        if((Message)radRequest.response == null)
            coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Timeout");
        else {
            String userName = radRequest.getAttributes(0).getValueString();
            switch(radRequest.response.getCode()){
                case ACCESS_ACCEPT:
                    // TODO: change this to a TicketGenerator
                    String newTicket = UnitConversion.ByteArray2Hex(ticketEngine.generateTicket(userName, coapReq.getSourceAddress()));
                    // TODO: Save Ticket here
                    coapReq.respond("Ticket [" + newTicket + "]");
                    break;
                case ACCESS_REJECT:
                    coapReq.respond(ResponseCode.UNAUTHORIZED, "Not Authorized");
                    break;
                default:
                    coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Bad RAD response");
                    break;
            }
        }
    }
}