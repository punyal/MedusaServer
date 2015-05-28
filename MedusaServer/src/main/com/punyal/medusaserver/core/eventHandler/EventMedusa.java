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

import com.punyal.medusaserver.core.eventHandler.EventConstants.*;
import java.util.EventObject;

/**
 * EventMedusa
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class EventMedusa extends EventObject {
    private Priority priority;
    private Type type;
    private Protocol protocol;
    private String message;
    private String messageTittle;
    
    public EventMedusa(Priority priority, Protocol protocol, Type type, Object source) {
        super(source);
        this.priority = priority;
        this.protocol = protocol;
        this.type = type;
    }
    
    public EventMedusa(Priority priority, Protocol protocol, Type type, String message,
            String title, Object source) {
        super(source);
        this.priority = priority;
        this.protocol = protocol;
        this.type = type;
        this.message = message;
        this.messageTittle = title;
    }
    
    /**
     * Event Type Setter
     * @param type of event
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Event Type Getter
     * @return type of event
     */
    public Type getType() {
        return this.type;
    }
    
    /**
     * Event Priority Setter
     * @param priority of event
     */
    public void setPriotity(Priority priority) {
        this.priority = priority;
    }
    
    /**
     * Event Priority Getter
     * @return priority of event
     */
    public Priority getPriority() {
        return this.priority;
    }
    
    /**
     * Protocol Setter
     * @param protocol source
     */
    public void setProtocol(Protocol protocol) {
        this.protocol = protocol;
    }
    
    /**
     * Protocol Getter
     * @return protocol source
     */
    public Protocol getProtocol() {
        return this.protocol;
    }
    
    /**
     * Message Setter
     * @param message to show 
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Message Getter
     * @return message
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Message Title Setter
     * @param title to show
     */
    public void setTitle(String title) {
        this.messageTittle = title;
    }
    
    /**
     * Message Title Getter
     * @return title text
     */
    public String getTitle() {
        return this.messageTittle;
    }
}