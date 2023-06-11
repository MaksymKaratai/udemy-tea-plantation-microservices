FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN addgroup -S tea && adduser -S tea -G tea

ARG LAYERS_DIR=./build/layers

COPY --chown=tea ${LAYERS_DIR}/dependencies/ ./
COPY --chown=tea ${LAYERS_DIR}/spring-boot-loader/ ./
COPY --chown=tea ${LAYERS_DIR}/snapshot-dependencies/ ./
COPY --chown=tea ${LAYERS_DIR}/application/ ./

USER tea

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} org.springframework.boot.loader.JarLauncher" ]
