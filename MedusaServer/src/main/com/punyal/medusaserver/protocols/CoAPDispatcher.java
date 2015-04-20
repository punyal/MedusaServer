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
import static com.punyal.jrad.core.radius.RADIUS.Type.*;
import com.punyal.medusaserver.core.db.Query;
import com.punyal.medusaserver.core.eventHandler.EventConstants;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import com.punyal.medusaserver.core.security.TicketEngine;
import java.util.Date;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class CoAPDispatcher {
    private CoAPDispatcher() {}
    
    public static void dispatchRequest(CoapExchange coapReq, Query dbQuery, TicketEngine ticketEngine, EventMessage globalEvent) {
        switch(coapReq.getRequestCode()) {
            case GET: // Request a Authenticator Code
                coapReq.respond(ticketEngine.generateAuthenticator(coapReq.getSourceAddress()));
                break;
            case PUT: // Request a valid Ticket
                String userName = null;
                String userPass = null;
                
                JSONObject json = (JSONObject)JSONValue.parse(coapReq.getRequestText());
                userName = (String) json.get(JSON_USER_NAME);
                userPass = (String) json.get(JSON_USER_PASSWORD);
                
                if(userName == null || userPass == null) {
                    coapReq.respond(ResponseCode.NOT_ACCEPTABLE, "Wrong user-password format");
                }else{
                    userPass = ticketEngine.checkUserPass(dbQuery, coapReq.getSourceAddress(), userName, userPass);
                    
                    ticketEngine.printList(ticketEngine.getTicketList()); // TODO: delete xD
                    
                    if(userPass == null) {
                        coapReq.respond(ResponseCode.UNAUTHORIZED, "Bad Encryption"); // Change this message to a generic to increase the security
                    } else {
                        RadiusAuthenticationThread rat = new RadiusAuthenticationThread(
                        EventConstants.Protocol.CoAP,
                        coapReq,
                        userName,
                        userPass);
                        rat.addListener(globalEvent);
                        rat.start();
                    }
                }
                break;
            default:
                coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Unvalid CoAP Code");
                break;
        }
    }
    
    public static void dispatchResponse(Message radRequest, CoapExchange coapReq, TicketEngine ticketEngine, Query dbQuery) {
        if((Message)radRequest.response == null)
            coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Timeout");
        else {            
            switch(radRequest.response.getCode()){
                case ACCESS_ACCEPT:
                    // TODO: Create the ticket and extra information with the RADIUS response
                    String userName = radRequest.getAttributeByType(USER_NAME).getValueString();
                    long timeout = Long.parseLong(radRequest.getAttributeByType(SESSION_TIMEOUT).getValueString());
                    
                    if(timeout > 0 )
                        timeout = (new Date()).getTime() + timeout;
                    else
                        timeout = (new Date()).getTime() + GENERIC_TICKET_TIMEOUT;
                    
                    String ticketInfo = ticketEngine.createTicket4User(dbQuery, coapReq.getSourceAddress(), userName, timeout);
                    
                    if(ticketInfo == null) {
                        coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Ticket Generation Error");
                    }
                    else {
                        coapReq.respond(ticketInfo);
                    }
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