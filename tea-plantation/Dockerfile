FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
RUN addgroup -S tea && adduser -S tea -G tea
COPY --chown=tea build/libs/tea-plantation-*.jar app.jar
USER tea

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar" ]
