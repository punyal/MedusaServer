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

import com.punyal.medusaserver.core.eventHandler.EventConstants.Type;
import java.util.EventObject;

public class EventMedusa extends EventObject {
    // extended parameters
    private int priority; // priority of events for event dispatcher
    private Type type;
    private String message;
    private String messageTittle;
    
    /**
     * Constructor of EventMedusa
     * @param priority to attend the event
     * @param type of the message
     * @param source the source of the event
     */
    public EventMedusa(int priority, Type type, Object source) {
        super(source);
        this.priority = priority;
        this.type = type;
    }
    
    /**
     * Constructor of EventMedusa
     * @param priority to attend the event
     * @param type of the message
     * @param message text of event to show
     * @param title title of event to show
     * @param source the source of the event
     */
    public EventMedusa(int priority, Type type, String message,
            String title, Object source) {
        super(source);
        this.priority = priority;
        this.type = type;
        this.message = message;
        this.messageTittle = title;
    }
    
    /**
     * Set Event Type
     * @param type of event
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Get Event Type
     * @return type of event
     */
    public Type getType() {
        return this.type;
    }
    
    /**
     * Set Event priority
     * @param priority of event
     */
    public void setPriotity(int priority) {
        this.priority = priority;
    }
    
    /**
     * Get Event priority
     * @return priority of event
     */
    public int getPriority() {
        return this.priority;
    }
    
    /**
     * Set Message of Event
     * @param message to show 
     */
    public void setMessage(String message) {
        this.message = message;
    }
    
    /**
     * Get Message of Event
     * @return message
     */
    public String getMessage() {
        return this.message;
    }
    
    /**
     * Set Message Title of Event
     * @param title to show
     */
    public void setTitle(String title) {
        this.messageTittle = title;
    }
    
    /**
     * Get Message Title of Event
     * @return title text
     */
    public String getTitle() {
        return this.messageTittle;
    }
}