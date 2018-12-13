package com.cristoferz.dnsproxy.main;

/**
 *
 * @author cristofer
 */
public class DNSConfig {
   private static DNSConfig instance;
   
   public static DNSConfig getInstance() {
      if (instance == null) {
         throw new IllegalStateException("DNSConfig not innitialized yet.");
      }
      return instance;
   }
   
   public static DNSConfig init(int port, String[] destinations, int timeout) {
      instance = new DNSConfig(port, destinations, timeout);
      return instance;
   }
   
   private final String[] destinations;
   private final int[] ports;
   private int port;
   private int timeout;

   public DNSConfig(int port, String[] destinations, int timeout) {
      this.destinations = new String[destinations.length];
      this.ports = new int[destinations.length];
      for (int i = 0; i < destinations.length; i++) {
         String[] parts = destinations[i].split(":");
         this.destinations[i] = parts[0];
         if (parts.length == 1) {
            this.ports[i] = 853;
         } else {
            this.ports[i] = Integer.parseInt(parts[1]);
         }
      }
      this.port = port;
      this.timeout = timeout;
   }
   
   public String[] getDestinations() {
      return destinations;
   }

   public int[] getPorts() {
      return ports;
   }

   public int getPort() {
      return port;
   }

   public int getTimeout() {
      return timeout;
   }

}
