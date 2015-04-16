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

import com.punyal.jrad.core.radius.Message;
import static com.punyal.jrad.core.radius.RADIUS.Code.*;
import com.punyal.medusaserver.core.db.Query;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import com.punyal.medusaserver.core.security.Randomizer;
import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.logger.Logger;
import com.punyal.medusaserver.protocols.*;
import com.punyal.medusaserver.utils.Packetizer;
import com.punyal.medusaserver.utils.UnitConversion;
import static org.eclipse.californium.core.coap.CoAP.*;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class EventHandler extends Thread {
    private final EventMessage globalEvent;
    private TicketEngine ticketEngine;
    private Randomizer randomizer;
    private Query dbQuery;
    
    private RADIUS radiusClient;
    private CoAP coapServer;
    
    
    
    /**
     * Constructor to set the dispatcher
     */
    public EventHandler() {
        this.globalEvent = new EventMessage() {
            @Override
            public void fireEvent(EventMedusa evt) {
               /* System.out.println("(" + evt.getPriority() + " | "
                        + evt.getType() + ") "
                        +"[" + evt.getTitle() + " : "
                        + evt.getMessage() + "]");
                */
                
                switch(evt.getProtocol().toString()) {
                    case "RADIUS":
//========================== MOVE THIS TO THE DISPATCHER =======================
Packetizer resolution = (Packetizer)evt.getSource();
Message radRes = (Message)resolution.Response;

switch(resolution.RequestProtocol.toString()) {
    case "CoAP": // Generate the appropiate response for CoAP with all the info
        CoapExchange coapReq = (CoapExchange)resolution.Request;
        if(evt.getMessage().equals("Timeout")) {
            Logger.normal("[CoAP] Ticket [Timeout] to (" + evt.getMessage() + ") @ " + coapReq.getSourceAddress() + ":" + coapReq.getSourcePort());
            coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Timeout");
        } else {
            String userName = radRes.getAttributes(0).getValueString();
            switch(radRes.response.getCode()){
                case ACCESS_ACCEPT:
                    // TODO: change this to a TicketGenerator
                    String newTicket = UnitConversion.ByteArray2Hex(ticketEngine.generateTicket(userName, coapReq.getSourceAddress()));
                    // TODO: Save Ticket here
                    Logger.normal("[CoAP] Ticket [" + newTicket + "] to (" + userName + ") @ " + coapReq.getSourceAddress() + ":" + coapReq.getSourcePort());
                    coapReq.respond("Ticket [" + newTicket + "]");
                    break;
                case ACCESS_REJECT:
                    Logger.normal("[CoAP] Ticket [Not Authorized] to (" + userName + ") @ " + coapReq.getSourceAddress() + ":" + coapReq.getSourcePort());
                    coapReq.respond(ResponseCode.UNAUTHORIZED, "Not Authorized");
                    break;
                default:
                    Logger.normal("[CoAP] Ticket [Bad RAD response] to (" + userName + ") @ " + coapReq.getSourceAddress() + ":" + coapReq.getSourcePort());
                    coapReq.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Bad RAD response");
                    break;
            }
        }
        
        
        break;
    case "REST": // Generate the appropiate response for CoAP with all the info
        break;
        default: throw new IllegalArgumentException("Unknown Protocol " + resolution.RequestProtocol.toString()); 
}
                
//==============================================================================
                        break;
                    case "CoAP":
                        CoapExchange coapReq = (CoapExchange)evt.getSource();
                        switch(coapReq.getRequestCode()) {
                            case GET: // Request a Authenticator Code
                                String newAuthenticator = UnitConversion.ByteArray2Hex(randomizer.generate16bytes());
                                // Save the information at this point
                                Logger.normal("[CoAP] Authenticator [" + newAuthenticator + "] to " + coapReq.getSourceAddress() + ":" + coapReq.getSourcePort());
                                coapReq.respond("Authenticator [" + newAuthenticator + "]");
                                break;
                            case PUT: // Request a valid Ticket
                                String[] userPass = coapReq.getRequestText().split("@");
                                if(userPass.length != 2) {
                                    Logger.normal("[CoAP] Ticket [Wrong user-password format] @"+ coapReq.getSourceAddress() + ":" + coapReq.getSourcePort());
                                    coapReq.respond(ResponseCode.NOT_ACCEPTABLE, "Wrong user-password format");
                                }else{
                                    // Check if the pass is valid
                                    
                                    System.out.println("Correct PassWord " + dbQuery.getPass4User(userPass[0]));
                                    
                                    RadiusAuthenticationThread rat = new RadiusAuthenticationThread(
                                        EventConstants.Protocol.CoAP,
                                        evt.getSource(),
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
                        
                        break;
                    case "REST":
                        System.out.println("Protocol REST");
                        break;
                    default: throw new IllegalArgumentException("Unknown Protocol " + evt.getProtocol().toString()); 
                }
            }
        };
    }

    @Override
    public void run() {
        System.out.println("Event Handler running...");
        /**
         * Initialization of all subsystems
         */
        ticketEngine = new TicketEngine();
        randomizer = new Randomizer();
        dbQuery = new Query(MySQL_USER, MySQL_USER_PASSWORD, MySQL_SERVER);
        
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