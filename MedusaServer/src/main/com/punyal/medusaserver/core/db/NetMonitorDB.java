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
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class NetMonitorDB {
    DBsql mySQL;
    
    public NetMonitorDB(Status status, String server, String dbname, String user, String password) {
        mySQL = new DBsql(status, this.getClass().getSimpleName(), server, dbname, user, password);
        this.resetDB();
    }
    
    public void resetDB() {
        mySQL.Update("truncate table links");
        mySQL.Update("truncate table nodes");
        mySQL.Update("truncate table updates");
        mySQL.Update("INSERT INTO `arrowhead_network`.`updates` (`command`, `updatetime`) VALUES ('refresh', NOW());");
    }
    
    
    public void addNode(String nodeName, String nodeType) {
        mySQL.Update("INSERT INTO `arrowhead_network`.`nodes` (`name`, `description`, `type`) VALUES ('"+nodeName+"', '', '"+nodeType+"');");
        mySQL.Update("INSERT INTO `arrowhead_network`.`updates` (`command`, `name`, `type`, `updatetime`) VALUES ('new', '"+nodeName+"', '"+nodeType+"',  NOW());");  
    }
    
    public void removeNode(String nodeName) {
        mySQL.Update("DELETE FROM `nodes` WHERE `name` = '"+nodeName+"';");
        mySQL.Update("INSERT INTO `arrowhead_network`.`updates` (`command`, `name`, `updatetime`) VALUES ('delete', '"+nodeName+"',  NOW());");
    }
    
    public void addLink(String from, String to) {
        mySQL.Update("INSERT INTO `arrowhead_network`.`links` (`from`, `to`) VALUES ('"+from+"', '"+to+"');");
        mySQL.Update("INSERT INTO `arrowhead_network`.`updates` (`command`, `from`, `to`, `updatetime`) VALUES ('new', '"+from+"', '"+to+"', NOW());");  
    }
    
    public void removeLink(String from, String to) {
        mySQL.Update("DELETE FROM `links` WHERE `from` = '"+from+"' AND `to` = '"+to+"';");
        mySQL.Update("INSERT INTO `arrowhead_network`.`updates` (`command`, `from`, `to`, `updatetime`) VALUES ('delete', '"+from+"', '"+to+"', NOW());");  
    }
    
}