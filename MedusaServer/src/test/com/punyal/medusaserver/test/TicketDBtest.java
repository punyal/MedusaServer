
package com.punyal.medusaserver.test;

import com.punyal.medusaserver.core.db.TicketDB;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import com.punyal.medusaserver.core.medusa.Status;
import com.punyal.medusaserver.core.security.Randomizer;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TicketDBtest {
    
    public static void main(String[] args) {
        try {
            System.out.println("# Test (START)");
            
            Randomizer randomizer = new Randomizer();
            
            Status status = new Status();
            TicketDB ticketDB = new TicketDB(status, MySQL_TICKET_SERVER, MySQL_TICKET_DBNAME, MySQL_TICKET_USER, MySQL_TICKET_USER_PASSWORD);
            
            System.out.println(ticketDB.newAuthenticator(InetAddress.getByName(RADIUS_SERVER_IP), "lskdjflksdjf"));
            
            
            System.out.println("# Test (STOP)");
        } catch (UnknownHostException ex) {
            Logger.getLogger(TicketDBtest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void zzz(int miliseconds) {
        try {
            sleep(miliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(TicketDBtest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
