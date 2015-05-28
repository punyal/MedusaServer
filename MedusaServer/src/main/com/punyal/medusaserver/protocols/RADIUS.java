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

import com.punyal.jrad.JRaDclient;
import static com.punyal.jrad.core.radius.RADIUS.DEFAULT_RADIUS_PORT;
import com.punyal.medusaserver.core.eventHandler.EventConstants.Priority;
import static com.punyal.medusaserver.core.eventHandler.EventConstants.Priority.HIGH;

/**
 * RADIUS
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class RADIUS extends JRaDclient {
    public static Priority PRIORITY = HIGH;
    public static int DEFAULT_PORT = DEFAULT_RADIUS_PORT;
    
    /**
     * Adaptor to get the default port of the protocol
     * @return default port
     */
    public static int getDefaultPort() {
        return DEFAULT_RADIUS_PORT;
    }
}