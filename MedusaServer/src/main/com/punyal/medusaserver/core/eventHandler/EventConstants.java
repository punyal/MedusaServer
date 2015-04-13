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

public class EventConstants {
    private EventConstants () {
        // prevent initialization
    }
    
    public static final int RADIUSpriority = 2;
    public static final int CoAPpriority   = 2;
    public static final int RESTpriority   = 2;
    
    public enum Type {
        UNKNOWN   (1),
        ERROR     (2),
        WARNING   (3),
        DEBUG     (4),
        NORMAL    (5);
        
        /* Type value */
        public final int value;
        
        Type(int value) {
            this.value = value;
        }
        
        public static Type valueOf(int value) {
            switch(value) {
                case 1: return UNKNOWN;
                case 2: return ERROR;
                case 3: return WARNING;
                case 4: return DEBUG;
                case 5: return NORMAL;
                default: throw new IllegalArgumentException("Unknown Event Type "+value); 
            }
        }
    }
    
}