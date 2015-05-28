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

/**
 * EventConstants
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class EventConstants {
    private EventConstants () {}
    
    /**
     * Event Protocol
     */
    public enum Protocol {
        RADIUS (0),
        CoAP   (1),
        REST   (2);
        public final int value;
        Protocol(int value) {
            this.value = value;
        }
        public static Protocol valueOf(int value) {
            switch(value) {
                case 0: return RADIUS;
                case 1: return CoAP;
                case 2: return REST;
                default: throw new IllegalArgumentException("Unknown Event Source "+value); 
            }
        }
    }
    
    /**
     * Event Priority
     */
    public enum Priority {
        CRITICAL  (0),
        HIGH      (1),
        NORMAL    (2),
        LOW       (3);
        public final int value;
        Priority(int value) {
            this.value = value;
        }
        public static Priority valueOf(int value) {
            switch(value) {
                case 0: return CRITICAL;
                case 1: return HIGH;
                case 2: return NORMAL;
                case 3: return LOW;
                default: throw new IllegalArgumentException("Unknown Event Priority "+value); 
            }
        }
    }
    
    /**
     * Event Type
     */
    public enum Type {
        UNKNOWN   (1),
        ERROR     (2),
        WARNING   (3),
        DEBUG     (4),
        NORMAL    (5);
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