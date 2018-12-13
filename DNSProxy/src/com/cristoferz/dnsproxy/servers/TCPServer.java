/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cristoferz.dnsproxy.servers;

import com.cristoferz.dnsproxy.main.DNSConfig;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cristofer
 */
public class TCPServer extends Thread {
   
   @Override
   public void run() {
      try {
         ServerSocket listener = new ServerSocket(DNSConfig.getInstance().getPort());
         Logger.getLogger(TCPServer.class.getName()).log(Level.INFO, "TCP Server started on port {0}", DNSConfig.getInstance().getPort());
         while(true) {
            Socket socket = listener.accept();
            Logger.getLogger(TCPServer.class.getName()).log(Level.INFO, "TCP connection from {0}", socket.getInetAddress());
            new TCPServerSocket(socket).start();
         }
      } catch (IOException ex) {
         Logger.getLogger(TCPServer.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
}
