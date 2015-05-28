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

/**
 * MedusaConstants
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class MedusaConstants {
    // Version Constants
    public static final int version = 0;
    public static final int subVersion = 2;
    
    // CoAP Configuration
    public static final int CoAP_TICKET_OPTION = 100;
    
    // Performance Constants
    public static long AUTHENTICATION_CODE_TIMEOUT = 10000; // 1000*10 = 10000(10s)
    public static long RADIUS_TIMEOUT = 1000; // 1000*1 (1s)
    public static long GENERIC_TICKET_TIMEOUT = 300000; // 1000*60*5 = 300000 (5m)
    
    // JSON Message Constants
    public static String JSON_USER_NAME = "userName";
    public static String JSON_USER_PASSWORD = "userPass";
    public static String JSON_TIME_TO_EXPIRE = "ExpireTime";
    public static String JSON_AUTHENTICATOR = "Authenticator";
    public static String JSON_MY_TICKET = "MyTicket";
    public static String JSON_TICKET = "Ticket";
    public static String JSON_ADDRESS = "Address";
    public static String JSON_INFO = "Info";
}