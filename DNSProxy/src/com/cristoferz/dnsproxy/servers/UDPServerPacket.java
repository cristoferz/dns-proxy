package com.cristoferz.dnsproxy.servers;

import com.cristoferz.dnsproxy.request.DoTRequester;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 *
 * @author cristofer
 */
public class UDPServerPacket extends Thread {

   private DatagramSocket socket;
   private DatagramPacket packet;

   public UDPServerPacket(DatagramSocket socket, DatagramPacket packet) {
      this.socket = socket;
      this.packet = packet;
   }

   @Override
   public void run() {
      try (DoTRequester req = new DoTRequester()) {
         try(OutputStream os = req.getOutputStream()) {
            try (InputStream is = req.getInputStream()) {
               // Generates 2-byte length to transform UDP request on TCP
               byte[] data = new byte[2];
               data[1] = (byte)(packet.getLength() & 0xFF);
               data[0] = (byte)((packet.getLength() >> 8) & 0xFF);
               os.write(data, 0, 2);
               
               // Send data as is after the 2-byte length
               os.write(packet.getData(), 0, packet.getLength());
               os.flush();
               
               byte[] buf = new byte[1024];
               int len = is.read(buf);
               // Removes the 2-byte length from TCP response at the beggining of the array via offset on Datagram packet
               socket.send(new DatagramPacket(buf, 2, len-2, packet.getAddress(), packet.getPort()));
            }
         }

      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
