FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
EXPOSE 8080
COPY target/cloud-registration-service-1.0.0.jar app.jar
USER 1001
ENTRYPOINT ["java", "-jar", "app.jar"]