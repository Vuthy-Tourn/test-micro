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
include(":spring-clouds:config-server")\n\
' > settings.gradle

COPY spring-clouds/config-server/ spring-clouds/config-server/
COPY config-repo/ config-repo/

RUN chmod +x gradlew && \
    ./gradlew :spring-clouds:config-server:bootJar \
    --no-daemon -x test -x javadoc

FROM --platform=linux/amd64 eclipse-temurin:25-jre-alpine AS runtime

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=builder /workspace/spring-clouds/config-server/build/libs/*.jar app.jar
COPY --from=builder /workspace/config-repo/ config-repo/

EXPOSE 16904

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
