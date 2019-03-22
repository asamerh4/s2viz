FROM quay.io/azavea/openjdk-gdal:2.4-jdk8-alpine

RUN apk add maven && cd / && git clone https://github.com/asamerh4/s2viz.git && cd /s2viz && mvn compile package

WORKDIR /s2viz

RUN mkdir work

EXPOSE 8585
ENTRYPOINT java -jar target/s2viz-rest-api-0.0.1-SNAPSHOT-jar-with-dependencies.jar

