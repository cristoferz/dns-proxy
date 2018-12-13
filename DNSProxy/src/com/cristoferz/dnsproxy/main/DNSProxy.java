package com.cristoferz.dnsproxy.main;

import com.cristoferz.dnsproxy.servers.TCPServer;
import com.cristoferz.dnsproxy.servers.UDPServer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cristofer
 */
public class DNSProxy {

   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) {
      // Default values
      String destinations = "1.1.1.1,1.0.0.1";
      int port = 53;
      int timeout = 1000;
      for(String arg : args) {
         if (arg.startsWith("-d=")) {
            destinations = arg.substring(3);
            Logger.getLogger(DNSProxy.class.getName()).log(Level.INFO, "Destinations setted to {0}", destinations);
         } else if (arg.startsWith("-p=")) {
            port = Integer.parseInt(arg.substring(3));
            Logger.getLogger(DNSProxy.class.getName()).log(Level.INFO, "Port setted to {0}", port);
         } else if (arg.startsWith("-t=")) {
            timeout = Integer.parseInt(arg.substring(3));
            Logger.getLogger(DNSProxy.class.getName()).log(Level.INFO, "Timeout setted to {0}", timeout);
         }
      }
      DNSConfig.init(port, destinations.split(","), timeout);
      new TCPServer().start();
      new UDPServer().start();
   }
   
}
