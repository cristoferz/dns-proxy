package com.cristoferz.dnsproxy.servers;

import com.cristoferz.dnsproxy.request.DoTRequester;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author cristofer
 */
public class TCPServerSocket extends Thread {

   private Socket socket;

   public TCPServerSocket(Socket socket) {
      this.socket = socket;
   }

   @Override
   public void run() {
      try (DoTRequester req = new DoTRequester()) {
         // Redirect all data received and sent through DoTRequester
         new Thread() {
            @Override
            public void run() {
               try (OutputStream os = socket.getOutputStream()) {
                  try (InputStream is = req.getInputStream()) {
                     byte[] buf = new byte[64];
                     int len;
                     while ((len = is.read(buf)) != -1) {
                        os.write(buf, 0, len);
                        
                     }
                  }
               } catch (SocketException ex) {
                  // Connection closed
               } catch (IOException ex) {
                  ex.printStackTrace();
               }
            }
         }.start();
         
         try (InputStream is = socket.getInputStream()) {
            try (OutputStream os = req.getOutputStream()) {
               byte[] buf = new byte[64];
               int len;
               while ((len = is.read(buf)) != -1) {
                  os.write(buf, 0, len);
               }
            }
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
