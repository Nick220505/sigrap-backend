FROM eclipse-temurin:21-jdk-jammy AS dev

WORKDIR /usr/src/app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN apt-get update && apt-get install -y wget curl && rm -rf /var/lib/apt/lists/* && \
    echo '#!/bin/sh\nwget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1' > /healthcheck.sh && \
    chmod +x /healthcheck.sh

EXPOSE 8080
EXPOSE 5005

HEALTHCHECK --interval=10s --timeout=5s --start-period=60s --retries=5 \
  CMD ["/healthcheck.sh"]

CMD ["./mvnw", "spring-boot:run", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005'"] 