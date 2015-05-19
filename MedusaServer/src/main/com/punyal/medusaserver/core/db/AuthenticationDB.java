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

import com.punyal.medusaserver.core.medusa.Status;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthenticationDB {
    private final String server;
    private final String dbname;
    private final String user;
    private final String password;
    
    public AuthenticationDB(Status status, String server, String dbname, String user, String password) {
        status.addNewDBStatus(this.getClass().getSimpleName());
        this.server = server;
        this.dbname = dbname;
        this.user = user;
        this.password = password;
    }
    
    public String getPass4User(String userName) {
        String toReturn = null;
        
        try {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT value FROM radcheck WHERE username=\"" + userName + "\" && attribute=\"Cleartext-Password\"")) {
                if(resultSet.next())
                    toReturn = resultSet.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AuthenticationDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return toReturn;
        /*
        
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://"+server+"/"+dbname, user, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            System.out.println(resultSet);
            statement.close();
            connection.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(DBsql.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
        
        
        
        ResultSet result = mySQL.Query("SELECT value FROM radcheck WHERE username=\"" + userName + "\" && attribute=\"Cleartext-Password\"");
        if(result != null) {
            try {
                if(result.next())
                    return result.getString(1);
            } catch (SQLException ex) {
            }
        }
        // System.err.println("NO correct SQL pass response");
        return null;*/
            
    }
}