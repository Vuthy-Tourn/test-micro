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
include(":common:common-domain")\n\
\n\
include(":microservices:consumer-service:consumer-main")\n\
include(":microservices:consumer-service:consumer-application:consumer-restapi")\n\
include(":microservices:consumer-service:consumer-domain:consumer-application-service")\n\
include(":microservices:consumer-service:consumer-domain:consumer-domain-core")\n\
include(":microservices:consumer-service:consumer-datapersistence")\n\
include(":microservices:consumer-service:consumer-message")\n\
' > settings.gradle

COPY common/ common/
COPY microservices/consumer-service/ microservices/consumer-service/

RUN chmod +x gradlew && \
    ./gradlew :microservices:consumer-service:consumer-main:bootJar \
    --no-daemon -x test -x javadoc

FROM --platform=linux/amd64 eclipse-temurin:25-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /workspace/microservices/consumer-service/consumer-main/build/libs/*.jar app.jar

EXPOSE 16804

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
