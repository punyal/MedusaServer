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

import com.punyal.medusaserver.core.db.AuthenticationDB;
import com.punyal.medusaserver.core.db.TicketDB;
import com.punyal.medusaserver.core.eventHandler.EventHandler;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.core.security.TicketEngine;

/**
 * GlobalVars
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class GlobalVars {
    private final TicketEngine ticketEngine;
    private final EventHandler evtHandler;
    private final Status status;
    private final AuthenticationDB authDB;
    private final TicketDB ticketDB;
    
    public GlobalVars() {
        status = new Status(); // Create and set the server status
        authDB = new AuthenticationDB(status, MySQL_AUTHENTICATION_SERVER, MySQL_AUTHENTICATION_DBNAME, MySQL_AUTHENTICATION_USER, MySQL_AUTHENTICATION_USER_PASSWORD);
        ticketDB = new TicketDB(status, MySQL_TICKET_SERVER, MySQL_TICKET_DBNAME, MySQL_TICKET_USER, MySQL_TICKET_USER_PASSWORD);
        ticketEngine = new TicketEngine(this); // Create a ticketEngine
        evtHandler = new EventHandler(this); // Create and Start the Event Handler
        evtHandler.start();
    }
    
    /**
     * TicketEngine Getter
     * @return Ticket Engine
     */
    public synchronized TicketEngine getTicketEngine() {
        return ticketEngine;
    }
    
    /**
     * Main Event Handler Getter
     * @return Event Handler
     */
    public synchronized EventHandler getHandler() {
        return evtHandler;
    }
    
    /**
     * Server Status Getter
     * @return Server Status
     */
    public synchronized Status getStatus() {
        return status;
    }
    
    /**
     * Authentication Data Base Getter
     * @return Authentication DB methods
     */
    public synchronized AuthenticationDB getAuthDB() {
        return authDB;
    }
    
    /**
     * Ticket Data Base Getter
     * @return Ticket DB methods
     */  
    public synchronized TicketDB getTicketDB() {
        return ticketDB;
    }
}
    