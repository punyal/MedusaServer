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
    public static String AUTHENTICATION_SECRET_KEY = "Arrowhead";
    
    // Performance Constants
    public static long AUTHENTICATION_CODE_TIMEOUT = 10000; // 1000*10 = 10000(10s)
    public static long RADIUS_TIMEOUT = 1000; // 1000*1 (1s)
    public static long GENERIC_TICKET_TIMEOUT = 30000; // 1000*60*10 = 600000 (10m)
    
    // Message Format
    public static String JSON_USER_NAME = "userName";
    public static String JSON_USER_PASSWORD = "userPass";
    public static String JSON_TIME_TO_EXPIRE = "ExpireTime";
    public static String JSON_AUTHENTICATOR = "Authenticator";
    public static String JSON_TICKET = "Ticket";
    
    // RADIUS Configuration
    public static String RADIUS_SERVER_IP = "localhost";
    public static int RADIUS_SERVER_PORT = DEFAULT_RADIUS_PORT;
    public static String RADIUS_SECRET_KEY = "testing123";
    
    // MySQL Configuration
    public static String MySQL_USER = "root";
    public static String MySQL_USER_PASSWORD = "Arrowhead2015&&";
    public static String MySQL_SERVER = "localhost";
}