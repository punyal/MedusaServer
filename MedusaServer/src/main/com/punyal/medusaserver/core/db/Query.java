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
package com.punyal.medusaserver.core.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Query {
    DBsql mySQL;
    
    public Query(String user, String password, String server) {
        mySQL = new DBsql(user, password, server);
    }
    
    public String getPass4User(String userName) {
        ResultSet result = mySQL.Query("SELECT value FROM radcheck WHERE username=\"" + userName + "\" && attribute=\"Cleartext-Password\"");
        if(result != null) {
            try {
                if(result.next())
                    return result.getString(1);
            } catch (SQLException ex) {
            }
        }
        System.err.println("NO correct SQL pass response");
        return null;
            
    }
}