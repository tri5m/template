#FROM eclipse-temurin:17.0.11_9-jre-jammy
FROM ibm-semeru-runtimes:open-17.0.10_7-jre-jammy

MAINTAINER trifolium <trifolium.wang@gmail.com>

LABEL app="template-service" version="1.0-SNAPSHOT" by="trifolium"

ENV APP_VERSION 1.0-SNAPSHOT

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone \
    && mkdir /app \
    && mkdir /app/logs

WORKDIR /app

COPY services/template-service/target/template-service.jar /app/app.jar
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:InitialRAMPercentage=50.0 -XX:MaxRAMPercentage=80.0"
#CMD java -jar /app/app.jar
ENTRYPOINT ["java", "${JAVA_OPTS}", "-jar", "app.jar"]

EXPOSE 8080