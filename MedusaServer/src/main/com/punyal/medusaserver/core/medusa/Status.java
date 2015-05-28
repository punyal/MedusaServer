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

import java.util.ArrayList;

/**
 * Status
 * @author Pablo Puñal Pereira {@literal (pablo @ punyal.com)}
 * @version 0.2
 */
public class Status {
    private final ArrayList<Protocol> protocolList;
    private final ArrayList<DataBase> dbList;
    
    public Status() {
        protocolList = new ArrayList<>();
        dbList = new ArrayList<>();
    }
    
    @Override
    public String toString() {
        String toPrint = "\n\n====[Medusa Server Status]====\n";
        toPrint += " Version: "+MedusaConstants.version+"."+MedusaConstants.subVersion+"\n";
        // DataBases status:
        toPrint += "\n -.DataBases.-\n";
        toPrint = dbList.stream().map((dbList1) -> " " + dbList1.getName() + " - " + (dbList1.getStatus() ? "Connected" : "Disconnected") + "\n").reduce(toPrint, String::concat);
        // Protocol status:
        toPrint += "\n -.Protocols.-\n";
        toPrint = protocolList.stream().map((protocolList1) -> " " + protocolList1.getName() + " - " + (protocolList1.getStatus() ? "ON" : "OFF") + "\n").reduce(toPrint, String::concat);
        toPrint += "==============================\n";
        return toPrint;
    }
    
    /**
     * Add New Protocol Status
     * @param medusaProtocol Protocol to add
     */
    public void addNewProtocolStatus(String medusaProtocol) {
        Protocol protocol = new Protocol(medusaProtocol);
        protocol.setStatus(true);
        if (findProtocolStatus(medusaProtocol) == null)
            protocolList.add(protocol);
        else
            System.err.println("Two "+medusaProtocol+" are running!");
    }
    
    /**
     * ProtocolStatus Setter
     * @param medusaProtocol Protocol
     * @param status on/off
     */
    public void setProtocolStatus(String medusaProtocol, boolean status) {
        Protocol protocol = findProtocolStatus(medusaProtocol);
        if (protocol != null) {
            protocol.setStatus(status);
        }
    }
    
    /**
     * Find Protocol Status
     * @param protocolName Protocol
     * @return Protocol
     */
    private Protocol findProtocolStatus(String protocolName) {
        if (protocolList.isEmpty()) return null;
        for (Protocol protocolList1 : protocolList) {
            if (protocolList1.getName().equals(protocolName)) {
                return protocolList1;
            }
        }
        return null;
    }
    
    /**
     * Add New DataBase Status
     * @param medusaDB name
     */
    public void addNewDBStatus(String medusaDB) {
        DataBase db = new DataBase(medusaDB);
        db.setStatus(true);
        if (findDBStatus(medusaDB) == null)
            dbList.add(db);
        else
            System.err.println("Two "+medusaDB+" are running!");
    }
    
    /**
     * DB Status Setter
     * @param medusaDB name
     * @param status on/off
     */
    public void setDBStatus(String medusaDB, boolean status) {
        DataBase db = findDBStatus(medusaDB);
        if (db != null) {
            db.setStatus(status);
        }
    }
    
    /**
     * Find DB Status
     * @param dbName to find
     * @return DB
     */
    private DataBase findDBStatus(String dbName) {
        if (dbList.isEmpty()) return null;
        for (DataBase dbList1 : dbList) {
            if (dbList1.getName().equals(dbName)) {
                return dbList1;
            }
        }
        return null;
    }
    
    /**
     * Protocol class definition
     */
    class Protocol {
        private final String name;
        private boolean status;
        
        public Protocol(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setStatus(boolean status) {
            this.status = status;
        }
        
        public boolean getStatus() {
            return status;
        }
        
    }
    
    /**
     * Database class definition
     */
    class DataBase {
        private final String name;
        private boolean status;
        
        public DataBase(String name) {
            this.name = name;
        }
        
        public String getName() {
            return name;
        }
        
        public void setStatus(boolean status) {
            this.status = status;
        }
        
        public boolean getStatus() {
            return status;
        }
        
    }
}