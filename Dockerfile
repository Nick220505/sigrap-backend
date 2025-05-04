FROM bellsoft/liberica-openjdk-debian:24 AS dev

WORKDIR /usr/src/app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN apt-get update && apt-get install -y wget curl && apt-get clean && rm -rf /var/lib/apt/lists/* && \
    echo '#!/bin/sh\nwget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1' > /usr/local/bin/healthcheck.sh && \
    chmod +x /usr/local/bin/healthcheck.sh

EXPOSE 8080
EXPOSE 5005

HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=5 \
  CMD ["sh", "/usr/local/bin/healthcheck.sh"]

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"] 