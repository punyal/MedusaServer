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
package com.punyal.medusaserver.core.medusa;

import static com.punyal.jrad.core.radius.RADIUS.DEFAULT_RADIUS_PORT;

public class Configuration {
    private Configuration() {} // Prevents initialization
    
    // Security Configuration
    public static String AUTHENTICATION_SECRET_KEY = "";
        
    // RADIUS Configuration
    public static String RADIUS_SERVER_IP = "";
    public static int RADIUS_SERVER_PORT = DEFAULT_RADIUS_PORT;
    public static String RADIUS_SECRET_KEY = "";
    
    // MySQL Authentication Configuration
    public static String MySQL_AUTHENTICATION_USER = "";
    public static String MySQL_AUTHENTICATION_USER_PASSWORD = "";
    public static String MySQL_AUTHENTICATION_SERVER = "";
    public static String MySQL_AUTHENTICATION_DBNAME = "";
    
    // MySQL NETMONITOR Configuration
    public static String MySQL_NETMONITOR_USER = "";
    public static String MySQL_NETMONITOR_USER_PASSWORD = "";
    public static String MySQL_NETMONITOR_SERVER = "";
    public static String MySQL_NETMONITOR_DBNAME = "";
    
    // MySQL Ticket Configuration
    public static String MySQL_TICKET_USER = "";
    public static String MySQL_TICKET_USER_PASSWORD = "";
    public static String MySQL_TICKET_SERVER = "";
    public static String MySQL_TICKET_DBNAME = "";
    
    // CoAP Configuration
    public static final int CoAP_TICKET_OPTION = 100;
}