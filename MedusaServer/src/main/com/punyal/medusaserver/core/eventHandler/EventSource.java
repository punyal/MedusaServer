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

import javax.swing.event.EventListenerList;

public class EventSource {
    protected EventListenerList listenerList = new EventListenerList();
    
    /**
     * Add a listener to the EventSource
     * @param listener to add
     */
    public void addEventListener(EventMessage listener) {
        listenerList.add(EventMessage.class, listener);
    }
    
    /**
     * Remove a listener from the EventSource
     * @param listener to remove
     */
    public void removeEventListener(EventMessage listener) {
        listenerList.remove(EventMessage.class, listener);
    }
    
    /**
     * Method to execute when an event appears
     * @param evt event info
     */
    public void newEvent(EventMedusa evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i=0; i<listeners.length; i=i+2) {
            if(listeners[i] == EventMessage.class) {
                ((EventMessage) listeners[i+1]).fireEvent(evt);
            }
        }
    }
}