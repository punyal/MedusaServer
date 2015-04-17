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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBsql {
    private static final Logger LOGGER = Logger.getLogger(DBsql.class.getCanonicalName());
    private Connection connection;
    
    public DBsql(String user, String password, String server) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://"+server+"/rd", user, password);
        } catch (SQLException ex) {
             LOGGER.log(Level.WARNING, "Unable to connect to database");
        }
    }
    
    public ResultSet Query(String query) {
        Statement statement;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.createStatement();
                
                if(statement != null) {
                    try {
                        resultSet = statement.executeQuery(query);
                    } catch(SQLException e) {
                        LOGGER.log(Level.WARNING, "Unable to create the Query");
                    }
                } else {
                    System.err.println("Null statement");
                }
                
            } catch (SQLException e) {
                System.err.println("Unable to create statement");
            }
        }
        return resultSet;
    }
}