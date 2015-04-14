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

import com.punyal.jrad.core.network.events.MessageListenerInt;
import com.punyal.jrad.core.radius.Message;
import com.punyal.medusaserver.core.eventHandler.EventConstants;
import static com.punyal.medusaserver.core.eventHandler.EventConstants.Type.*;
import com.punyal.medusaserver.core.eventHandler.EventMedusa;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import com.punyal.medusaserver.protocols.RADIUS;
import java.util.EventObject;

/**
 * Class to Convert between different Unit/Object types
 */
public class ClassParser {
    // Prevent Initialization
    private ClassParser() {}
    
    public static MessageListenerInt eventMedusa2jRAD (EventMessage eventMessage) {
        MessageListenerInt eventJRaD = new MessageListenerInt() {

            @Override
            public void newIncomingMessage(EventObject evt) {
                // There are two diferent responses: valid or timeout
                EventMedusa newEvt;
                if(((Message)evt.getSource()).response == null) { // Timeout
                    newEvt = new EventMedusa(RADIUS.PRIORITY,
                        EventConstants.Protocol.RADIUS,
                        ERROR,
                        "Timeout",
                        "RADIUS Client",
                        evt);
                } else {
                    newEvt = new EventMedusa(RADIUS.PRIORITY,
                        EventConstants.Protocol.RADIUS,
                        NORMAL,
                        ((Message)evt.getSource()).response.getCode().toString(),
                        "RADIUS Client",
                        evt);
                }
                eventMessage.fireEvent(newEvt);
            }
        };
        return eventJRaD;
    }
    
}
