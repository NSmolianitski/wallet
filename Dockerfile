FROM gradle:latest as builder
WORKDIR /app
COPY build.gradle settings.gradle /app/
COPY src /app/src
RUN gradle build --no-daemon --scan

FROM eclipse-temurin:17-jre-alpine
ENV APP_HOME=/app
WORKDIR $APP_HOME
COPY --from=builder /app/build/libs/wallet.jar $APP_HOME/wallet.jar
EXPOSE 8080
CMD ["java", "-jar", "wallet.jar"]