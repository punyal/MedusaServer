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

import static com.punyal.medusaserver.core.medusa.MedusaConstants.AUTHENTICATION_CODE_TIMEOUT;
import com.punyal.medusaserver.core.medusa.Status;
import java.net.InetAddress;
import java.util.Date;

public final class TicketDB {
    DBsql mySQL;
    
    public TicketDB(Status status, String server, String dbname, String user, String password) {
        mySQL = new DBsql(status, this.getClass().getSimpleName(), server, dbname, user, password);
        this.resetDB();
    }
    
    public void resetDB() {
        mySQL.Update("truncate table users");
    }
    
    public int newAuthenticator(InetAddress address, String authenticator) {
        mySQL.Update("INSERT INTO `ticket_engine`.`users` (`address`, `authenticator`, `expire_time`) VALUES ('"+address.toString().split("/")[1]+"', '"+authenticator+"', NOW());");
        System.out.println(new Date((new Date()).getTime() + (AUTHENTICATION_CODE_TIMEOUT)));
        return mySQL.Update("INSERT INTO `ticket_engine`.`users` (`address`, `authenticator`, `expire_time`) VALUES ('"+address.toString().split("/")[1]+"', '"+authenticator+"', (NOW()+INTERVAL "+AUTHENTICATION_CODE_TIMEOUT/1000+" SECOND));");
    }
    
}