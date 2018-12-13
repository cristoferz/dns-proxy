package com.cristoferz.dnsproxy.request;

import com.cristoferz.dnsproxy.main.DNSConfig;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author cristofer
 */
public class DoTRequester implements Closeable {

   private Socket socket;

   public DoTRequester() throws IOException {
      connect();
   }

   private void connect() throws IOException {
      SSLSocketFactory ssf = (SSLSocketFactory) SSLSocketFactory.getDefault();
      String[] destinations = DNSConfig.getInstance().getDestinations();
      int[] ports = DNSConfig.getInstance().getPorts();
      for (int i = 0; i < destinations.length; i++) {
         try {
            Logger.getLogger(DoTRequester.class.getName()).log(Level.FINER, "Connecting to {0}:{1}", new Object[] {destinations[i], ports[i]});
            Socket tcpSocket = new Socket();
            tcpSocket.connect(new InetSocketAddress(destinations[i], ports[i]), DNSConfig.getInstance().getTimeout());
            this.socket = ssf.createSocket(tcpSocket, destinations[i], ports[i], true);
            return;
         } catch (SocketTimeoutException ex) {
            Logger.getLogger(DoTRequester.class.getName()).log(Level.INFO, "Connection failed to {0}:{1}", new Object[] {destinations[i], ports[i]});
         }
      }
   }

   public OutputStream getOutputStream() throws IOException {
      return socket.getOutputStream();
   }

   public InputStream getInputStream() throws IOException {
      return socket.getInputStream();
   }

   @Override
   public void close() throws IOException {
      socket.close();
   }

}
