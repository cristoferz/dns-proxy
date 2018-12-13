FROM java:8

EXPOSE 53

RUN apt-get update
RUN apt-get install -y ant
RUN apt-get clean

COPY DNSProxy/ /usr/src/dns-proxy

RUN ant -f /usr/src/dns-proxy -Dnb.internal.action.name=build jar

WORKDIR /usr/src/dns-proxy/dist

ENTRYPOINT ["java", "-jar", "DNSProxy.jar"]
CMD []
