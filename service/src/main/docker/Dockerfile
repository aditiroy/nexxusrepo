#FROM dockercentral.it.att.com:5100/${namespace}/anapsix/alpine-java:jre8
#FROM anapsix/alpine-java:jre8
#FROM dockercentral.it.att.com:5100/com.att.ajsc.public/anapsix/alpine-java:jre8
FROM dockercentral.it.att.com:4278/com.att.salesmarketing.core.saco.prod/alpine:3.14
ARG http_proxy="http://proxy.conexus.svc.local:3128"
ARG https_proxy="http://proxy.conexus.svc.local:3128"

RUN  apk update \
  && apk upgrade \
  && apk add ca-certificates \
  && update-ca-certificates \
  && apk add --update coreutils && rm -rf /var/cache/apk/*   \ 
  && apk add --update openjdk8 tzdata curl unzip bash \
  && apk add --no-cache nss \
  && rm -rf /var/cache/apk/* \
  && apk add busybox-extras \
  && apk add --update openssl && rm -rf /var/cache/apk/*   \
  && apk add wget 
  

ENV JAVA_HOME /usr/lib/jvm/java-1.8-openjdk
ENV PATH $PATH:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin

ENV AJSC_HOME=/opt/att/ajsc
ENV AJSC_CONFIG_HOME=${AJSC_HOME}/config

COPY jfxrt.jar /jfxrt.jar
COPY startService.sh /startService.sh
RUN chmod 777 /startService.sh && dos2unix /startService.sh

ADD @project.build.finalName@-@assembly.id@.tar.gz app.jar
#ADD @project.build.finalName@-@assembly.id@.tar.gz ${AJSC_HOME}

ADD prometheus-java-agent.jar monitoring.jar
ADD ilib.jar ilib.jar
ADD hfrapm-agent.jar glowroot.jar

# COPY lib/jfxrt.jar usr/lib/jvm/java-1.8-openjdk/jre/lib/ext

RUN (cd app.jar/@project.build.finalName@;tar -cf - src) | (cd ../..;tar -xf -) \
&& (cd app.jar/@project.build.finalName@;tar -cf - opt/att/ajsc/lib) | (cd ../..;tar -xf -) \
&& (cd app.jar/@project.build.finalName@;tar -cf - opt/att/ajsc/config) | (cd ../..;tar -xf -) 
RUN (mv monitoring.jar opt/att/ajsc/lib)
RUN (mv ilib.jar opt/att/ajsc/lib)
RUN (mv glowroot.jar opt/att/ajsc/lib)
RUN (rm -rf app.jar)

#ADD athd1a1.snt.bst.bls.com.cer /usr/local/share/ca-certificates/athd1a1.snt.bst.bls.com.cer
#ADD athd1a1.snt.bst.bls.com_new.cer /usr/local/share/ca-certificates/athd1a1.snt.bst.bls.com_new.cer
#ADD athd1a1_root_ca_1.cer /usr/local/share/ca-certificates/athd1a1_root_ca_1.cer

#RUN chmod 644 /usr/local/share/ca-certificates/athd1a1.snt.bst.bls.com.cer && update-ca-certificates
#RUN chmod 644 /usr/local/share/ca-certificates/athd1a1.snt.bst.bls.com_new.cer && update-ca-certificates
#RUN chmod 644 /usr/local/share/ca-certificates/athd1a1_root_ca_1.cer && update-ca-certificates

#RUN (keytool -importcert -file athd1a1.snt.bst.bls.com.cer -keystore /usr/lib/jvm/default-jvm/jre/lib/security/cacerts -storepass changeit -alias USRPa -noprompt )
#RUN (keytool -importcert -file athd1a1.snt.bst.bls.com_new.cer -keystore /usr/lib/jvm/default-jvm/jre/lib/security/cacerts -storepass changeit -alias USRPb -noprompt )
#RUN (keytool -importcert -file athd1a1_root_ca_1.cer -keystore /usr/lib/jvm/default-jvm/jre/lib/security/cacerts -storepass changeit -alias USRPc -noprompt )

ENTRYPOINT /startService.sh
