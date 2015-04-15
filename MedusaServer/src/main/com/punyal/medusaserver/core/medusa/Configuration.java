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
    
    public static String RADIUS_SERVER_IP = "192.168.0.111";
    public static int RADIUS_SERVER_PORT = DEFAULT_RADIUS_PORT;
    public static String RADIUS_SECRET_KEY = "RADIUSoffice";
    
}