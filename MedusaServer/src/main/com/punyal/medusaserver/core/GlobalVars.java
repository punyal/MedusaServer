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
package com.punyal.medusaserver.core;

import com.punyal.medusaserver.core.eventHandler.EventHandler;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.core.security.TicketEngine;

public class GlobalVars {
    private final TicketEngine ticketEngine;
    private final EventHandler evtHandler;
    private final Status status;
    
    public GlobalVars() {
        status = new Status(); // Create and set the server status
        ticketEngine = new TicketEngine(); // Create a ticketEngine
        evtHandler = new EventHandler(status, ticketEngine); // Create and Start the Event Handler
        evtHandler.start();
    }
    
    public TicketEngine getTicketEngine() {
        return ticketEngine;
    }
    
    public EventHandler getHandler() {
        return evtHandler;
    }
    
    public Status getStatus() {
        return status;
    }
}