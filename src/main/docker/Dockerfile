FROM openjdk:17-oracle

RUN useradd -ms /bin/sh hs
WORKDIR /home/hs

ADD entrypoint.sh entrypoint.sh
RUN chmod 755 entrypoint.sh && chown hs:hs entrypoint.sh
USER hs

ENTRYPOINT ["./entrypoint.sh"]

EXPOSE 9000 5701/udp

ADD *.jar app.jar
