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
        for (int i=0; i<dbList.size(); i++) {
            toPrint += " "+dbList.get(i).getName()+ " - "+(dbList.get(i).getStatus()?"Connected":"Disconnected")+"\n";
        }
        
        // Protocol status:
        toPrint += "\n -.Protocols.-\n";
        for (int i=0; i<protocolList.size(); i++) {
            toPrint += " "+protocolList.get(i).getName()+ " - "+(protocolList.get(i).getStatus()?"ON":"OFF")+"\n";
        }
        
        toPrint += "==============================\n";
        return toPrint;
    }
    
    public void addNewProtocolStatus(String medusaProtocol) {
        Protocol protocol = new Protocol(medusaProtocol);
        protocol.setStatus(true);
        if (findProtocolStatus(medusaProtocol) == null)
            protocolList.add(protocol);
        else
            System.err.println("Two "+medusaProtocol+" are running!");
    }
    
    public void setProtocolStatus(String medusaProtocol, boolean status) {
        Protocol protocol = findProtocolStatus(medusaProtocol);
        if (protocol != null) {
            protocol.setStatus(status);
        }
    }
    
    private Protocol findProtocolStatus(String protocolName) {
        if (protocolList.isEmpty()) return null;
        for (Protocol protocolList1 : protocolList) {
            if (protocolList1.getName().equals(protocolName)) {
                return protocolList1;
            }
        }
        return null;
    }
    
    public void addNewDBStatus(String medusaDB) {
        DataBase db = new DataBase(medusaDB);
        db.setStatus(true);
        if (findDBStatus(medusaDB) == null)
            dbList.add(db);
        else
            System.err.println("Two "+medusaDB+" are running!");
    }
    
    public void setDBStatus(String medusaDB, boolean status) {
        DataBase db = findDBStatus(medusaDB);
        if (db != null) {
            db.setStatus(status);
        }
    }
    
    private DataBase findDBStatus(String dbName) {
        if (dbList.isEmpty()) return null;
        for (DataBase dbList1 : dbList) {
            if (dbList1.getName().equals(dbName)) {
                return dbList1;
            }
        }
        return null;
    }
    
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