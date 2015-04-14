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

import com.punyal.jrad.JRaDclient;
import com.punyal.jrad.core.network.events.MessageListenerInt;
import com.punyal.medusaserver.core.eventHandler.EventConstants.Protocol;
import java.util.EventObject;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class RadiusAuthenticationThread extends Thread {
    private Protocol protocol;
    private Object responder;
    private String userName;
    private String userPass;
    
    private ExecutorService executor;
    private Future<String> future;
    
    
    
    public RadiusAuthenticationThread(Protocol protocol, Object responder, String userName, String userPass) {
        this.protocol = protocol;
        this.responder = responder;
        this.userName = userName;
        this.userPass = userPass;
    }
    
    @Override
    public void run() {
        // Thread Timeout staff
        executor = Executors.newSingleThreadExecutor();
        future = executor.submit(new Task());
        try {
            System.out.println("Started..");
            try {
                future.get(2, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException ex) {
            System.out.println("InterruptedException | ExecutionException");
                // Return Error
                switch(this.protocol) {
                    case CoAP:
                        ((CoapExchange)responder).respond(ResponseCode.INTERNAL_SERVER_ERROR, "Error");
                        break;
                    case REST:
                        break;
                }
            }
            System.out.println("Normal Response");
            // Return Normal response
            switch(this.protocol) {
                case CoAP:
                    ((CoapExchange)responder).respond("Ticket");
                    break;
                case REST:
                    break;
            }
        } catch (TimeoutException e) {
            System.out.println("TimeoutException");
            // Return Error
            switch(this.protocol) {
                case CoAP:
                    ((CoapExchange)responder).respond(ResponseCode.INTERNAL_SERVER_ERROR, "Error");
                    break;
                case REST:
                    break;
            }
        }        
        executor.shutdownNow();    
    }
}

class Task implements Callable<String> {
    private boolean waiting = true;
    
    private JRaDclient radiusClient;
    
    
    @Override
    public String call() throws Exception {
        radiusClient = new JRaDclient();
        radiusClient.setSecretKey("RADIUSoffice");
        radiusClient.setServer("192.168.0.111", RADIUS.DEFAULT_PORT);
        radiusClient.addListener(new MessageListenerInt() {

            @Override
            public void newIncomingMessage(EventObject evt) {
                System.out.println("New incoming!!");
                TaskStop();
            }
        });
        radiusClient.authenticate("mulle", "mulle");
        while(true) {
            if(this.waiting == false) break;
        }
        return "RADIUS response";
    }
    
    public Task() {
        
    }
    
    public void TaskStop() {
        this.waiting = false;
    }
}