
package com.punyal.medusaserver.test;

import com.punyal.medusaserver.core.db.NetMonitorDB;
import static com.punyal.medusaserver.core.medusa.Configuration.*;
import com.punyal.medusaserver.core.medusa.Status;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetDBtest {
    
    public static void main(String[] args) {
        System.out.println("# Test (START)");
        Status status = new Status();
        NetMonitorDB netDB = new NetMonitorDB(status, MySQL_NETMONITOR_SERVER, MySQL_NETMONITOR_DBNAME, MySQL_NETMONITOR_USER, MySQL_NETMONITOR_USER_PASSWORD);
   
        System.out.println("Reset DB");
        netDB.resetDB();
        zzz(2000);
        System.out.println("Add TempServer");
        netDB.addNode("TempServer", "server");
        zzz(2000);
        System.out.println("Add Mulles");
        netDB.addNode("Mulle1", "embedded");
        netDB.addLink("TempServer", "Mulle1");
        netDB.addNode("Mulle2", "embedded");
        netDB.addLink("TempServer", "Mulle2");
        netDB.addNode("Mulle3", "embedded");
        netDB.addLink("TempServer", "Mulle3");
        netDB.addNode("Mulle4", "embedded");
        netDB.addLink("TempServer", "Mulle4");
        netDB.addNode("Mulle5", "embedded");
        netDB.addLink("TempServer", "Mulle5");
        netDB.addNode("Mulle6", "embedded");
        netDB.addLink("TempServer", "Mulle6");
        netDB.addNode("Mulle7", "embedded");
        netDB.addLink("TempServer", "Mulle7");
        netDB.addNode("Mulle8", "embedded");
        netDB.addLink("TempServer", "Mulle8");
        netDB.addNode("Mulle9", "embedded");
        netDB.addLink("TempServer", "Mulle9");
        netDB.addNode("Mulle10", "embedded");
        netDB.addLink("TempServer", "Mulle10");
        zzz(2000);
        System.out.println("Add Pablo");
        netDB.addNode("Pablo", "human");
        zzz(2000);
        System.out.println("Add Jens");
        netDB.addNode("Jens", "human");
        zzz(2000);
        netDB.removeLink("TempServer", "Mulle1");
        netDB.removeLink("TempServer", "Mulle2");
        netDB.removeLink("TempServer", "Mulle3");
        netDB.removeLink("TempServer", "Mulle4");
        netDB.removeLink("TempServer", "Mulle5");
        netDB.removeLink("TempServer", "Mulle6");
        netDB.removeLink("TempServer", "Mulle7");
        netDB.removeLink("TempServer", "Mulle8");
        netDB.removeLink("TempServer", "Mulle9");
        
        
        System.out.println("# Test (STOP)");
        
    }
    
    public static void zzz(int miliseconds) {
        try {
            sleep(miliseconds);
        } catch (InterruptedException ex) {
            Logger.getLogger(NetDBtest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
