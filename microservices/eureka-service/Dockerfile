FROM --platform=linux/amd64 eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /workspace

COPY gradlew gradlew.bat gradle.properties build.gradle ./
COPY gradle/ gradle/

RUN printf '\
plugins {\n\
    id "org.gradle.toolchains.foojay-resolver-convention" version "1.0.0"\n\
}\n\
rootProject.name = "stadoor"\n\
\n\
include(":microservices:eureka-service:common")\n\
include(":microservices:eureka-service:eureka-main")\n\
include(":microservices:eureka-service:eureka-application:eureka-restapi")\n\
include(":microservices:eureka-service:eureka-domain:eureka-application-service")\n\
include(":microservices:eureka-service:eureka-domain:eureka-domain-core")\n\
include(":microservices:eureka-service:eureka-datapersistence")\n\
include(":microservices:eureka-service:eureka-message")\n\
include(":microservices:eureka-service:eureka-webclient")\n\
' > settings.gradle

COPY microservices/eureka-service/ microservices/eureka-service/

RUN chmod +x gradlew && \
    ./gradlew :microservices:eureka-service:eureka-main:bootJar \
    --no-daemon -x test -x javadoc

FROM --platform=linux/amd64 eclipse-temurin:25-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /workspace/microservices/eureka-service/eureka-main/build/libs/*.jar app.jar

EXPOSE 16805

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
