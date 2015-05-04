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

import com.punyal.medusaserver.protocols.coap.CoAPDispatcher;
import com.punyal.jrad.core.radius.Message;
import com.punyal.medusaserver.core.db.AuthenticationDB;
import com.punyal.medusaserver.core.security.TicketEngine;
import com.punyal.medusaserver.utils.Packetizer;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RADIUSDispatcher {
    private RADIUSDispatcher () {}
    
    public static void dispatchResponse(Packetizer resolution, TicketEngine ticketEngine, AuthenticationDB authDB) {
        Message radResponse = (Message)resolution.Response;
        switch(resolution.RequestProtocol) {
            case CoAP:
                CoAPDispatcher.dispatchResponse(radResponse, (CoapExchange)resolution.Request, ticketEngine, authDB);
                break;
            case REST:
                break;
            default: throw new IllegalArgumentException("Unknown Protocol " + resolution.RequestProtocol); 
        }
    }
}