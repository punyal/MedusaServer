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
package com.punyal.medusaserver.utils;

import com.punyal.medusaserver.core.eventHandler.EventConstants.Protocol;

/**
 * Packetizer
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class Packetizer {
    public Object Request;
    public Protocol RequestProtocol;
    public Object Response;
    public Protocol ResponseProtocol;
    
    public Packetizer(Object Request, Protocol RequestProtocol,
               Object Response, Protocol ResponseProtocol) {
        this.Request = Request;
        this.RequestProtocol = RequestProtocol;
        this.Response = Response;
        this.ResponseProtocol = ResponseProtocol;
    }
}