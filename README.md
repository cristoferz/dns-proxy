# DNS Proxy
A DNS Proxy for DNS to DNS over TLS (DoT).

# How to use
To build the docker image run:

```
docker build -t dns-proxy .
```

This will compile source codes inside the container and build the image.

To run a container with default values for the destination parameter and publishing on port 5553 on localhost:

```
docker run --rm -p 5553:53 -p 5553:53/udp dns-proxy
```

To specify different destination DoT servers, you can use the `-d=` parameter passing all servers in `IP[:PORT]` format, separated by `,`.

```
docker run --rm -p 5553:53 -p 5553:53/udp dns-proxy -d=1.1.1.1:853,1.0.0.1
```

For a simple test you can use the `kdig` command:

```
kdig -d @127.0.0.1:5553 +tcp example.com
kdig -d @127.0.0.1:5553 example.com
```

This will test a direct connection to cloudflare server on port 853 (will not use the dns-proxy on this case, just for testing connectivty):

```
kdig -d @1.1.1.1 +tls-ca +tls-host=cloudflare-dns.com  example.com
```

# Implementation
This proxy is implemented using Java native Socket (TCP), DatagramSocket (UDP) and SSLSocketFactory for TLS 1.2 connection to DoT server. The proxy allows connections to it via TCP and UDP by default on port 53, but is possible to configure to use other ports. A list of destination DoT servers and ports can be specified but by default is used 1.1.1.1:853 and 1.0.0.1:853.

For TCP connections, a SocketServer waits for incoming connections on specified port. When a client connects, a connection is estabilished with a DoT server from the list, respecting the priority order and all the data sent and received is transfered between both connections. This way, all data is send exactly as is from one connection to another, switching from an unencrypted Socket connection to a TLS 1.2 connection.

For UDP connections, a DatagramSocket waits for incoming UDP packets on specified port, the same as configured for TCP connections. When a packet is received, a connection is estabilished with a DoT server from the list, respecting the priority order. To transform data to be send between the UDP and TCP protocols, is necessary to add 2 bytes on UDP data to send through TCP with the length of data to be send, as specified on RFC (https://tools.ietf.org/html/rfc1035#section-4.2.1). After sending these 2 bytes all received data can be send as is to the DoT server, through the TLS connection estabilished. For the response, is necessary to remove these 2 bytes from the beginning of response and then data can be send back to the origin of the incoming UDP packet.

## Choices
Using this approach was not necessary to interpret the DNS protocol as the messages can be send directly to the DoT server, just adding TLS on the transport. Only a simple modification was necessary to convert data from UDP to TCP. All classes used in Java are native to the language, so no addictional library was necessary. Using threads allow multiple simultaneous connections to be handled at the same time.

## Questions and Answers
What are the security concerns for this kind of service?
- Using this kind of service can simplify the use of encrypted communications on applications that doesn't have support, but keeps the security problems of unencrypted connections between the application and the proxy.  

Considering a microservice architecture; how would you see this the dns to
dns-over-tls proxy used?
- It can be deployed as multiple docker containers for running in a scalable structure, using Kubernetes or similar services such as AWS ECS. Then point DNS for the necessary servers to these DNS proxies, where multiple DNS can be specified for using multiple docker containers. Because of security problems mentioned before, is necessary to have a secure network between the applications and the DNS proxies, to avoid data tampering, man-in-the-middle attacks and all known security problems of unencripted connections.

What other improvements do you think would be interesting to add to the project?
- This implementation is already supporting multiple destination servers, but a failure on the first DNS servers on priority list will increse the response time for all requests. Servers can be temporarily disabled after a failed request, to avoid constant failures or timeouts.
- This implementation allows multiple concurrent requests.
