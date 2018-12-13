package com.cristoferz.dnsproxy.servers;

import com.cristoferz.dnsproxy.main.DNSConfig;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cristofer
 */
public class UDPServer extends Thread {
   private DatagramSocket socket;

   public UDPServer() {
   }

   @Override
   public void run() {
      try {
         socket = new DatagramSocket(DNSConfig.getInstance().getPort());
         Logger.getLogger(UDPServer.class.getName()).log(Level.INFO, "UDP Server started on port {0}", DNSConfig.getInstance().getPort());
         byte[] receiveData = new byte[1024];
         while(true) {
            DatagramPacket packet = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(packet);
            Logger.getLogger(UDPServer.class.getName()).log(Level.INFO, "UDP connection from {0}", packet.getAddress());
            new UDPServerPacket(socket, packet).start();           
         }
      } catch (IOException ex) {
         Logger.getLogger(UDPServer.class.getName()).log(Level.SEVERE, null, ex);
      }
      
   }
   
}
