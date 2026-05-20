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
include(":microservices:gateway-service:gateway-main")\n\
include(":microservices:gateway-service:gateway-application:gateway-restapi")\n\
include(":microservices:gateway-service:gateway-domain:gateway-application-service")\n\
include(":microservices:gateway-service:gateway-domain:gateway-domain-core")\n\
include(":microservices:gateway-service:gateway-datapersistence")\n\
include(":microservices:gateway-service:gateway-message")\n\
include(":microservices:gateway-service:gateway-infrastructure")\n\
' > settings.gradle

COPY common/ common/
COPY microservices/gateway-service/ microservices/gateway-service/

RUN chmod +x gradlew && \
    ./gradlew :microservices:gateway-service:gateway-main:bootJar \
    --no-daemon -x test -x javadoc

FROM --platform=linux/amd64 eclipse-temurin:25-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /workspace/microservices/gateway-service/gateway-main/build/libs/*.jar app.jar

EXPOSE 16803

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
