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
package com.punyal.medusaserver.protocols;

import com.punyal.jrad.core.network.events.MessageListenerInt;
import com.punyal.jrad.core.radius.Message;
import com.punyal.medusaserver.core.eventHandler.EventConstants;
import com.punyal.medusaserver.core.eventHandler.EventConstants.Protocol;
import com.punyal.medusaserver.core.eventHandler.EventMedusa;
import com.punyal.medusaserver.core.eventHandler.EventMessage;
import com.punyal.medusaserver.core.eventHandler.EventSource;
import com.punyal.medusaserver.core.medusa.Configuration;
import com.punyal.medusaserver.utils.Packetizer;
import java.util.EventObject;

public class RadiusAuthenticationThread extends Thread {
    private Protocol protocol;
    private Object responder;
    private String userName;
    private String userPass;
    
    private RADIUS radiusClient;
    
    private EventSource mlistener = new EventSource();
        
    public RadiusAuthenticationThread(Protocol protocol, Object responder, String userName, String userPass) {
        this.protocol = protocol;
        this.responder = responder;
        this.userName = userName;
        this.userPass = userPass;
        
        radiusClient = new RADIUS();
        radiusClient.setSecretKey(Configuration.RADIUS_SECRET_KEY);
        radiusClient.setServer(Configuration.RADIUS_SERVER_IP, Configuration.RADIUS_SERVER_PORT);
        radiusClient.addListener(new MessageListenerInt() {

            @Override
            public void newIncomingMessage(EventObject evt) {
                RadiusResponse(evt);
            }
        });
        
    }
    
    @Override
    public void run() {
        radiusClient.authenticate(userName, userPass);
    }
    
    private void RadiusResponse(EventObject evt) {
        EventMedusa newEvt;
        
        if(((Message)evt.getSource()).response == null){
            newEvt = new EventMedusa(RADIUS.PRIORITY,
                Protocol.RADIUS,
                EventConstants.Type.ERROR,
                "Timeout",
                "RADIUS Client",
                new Packetizer(this.responder , this.protocol, evt.getSource() , Protocol.RADIUS ) );
        } else {
            newEvt = new EventMedusa(RADIUS.PRIORITY,
                Protocol.RADIUS,
                EventConstants.Type.NORMAL,
                this.userName,
                "RADIUS Client",
                new Packetizer(this.responder , this.protocol, evt.getSource() , Protocol.RADIUS ) );
        }
        mlistener.newEvent(newEvt);
    }
    
    public void addListener(EventMessage listener) {
        mlistener.addEventListener(listener);
    }
}
