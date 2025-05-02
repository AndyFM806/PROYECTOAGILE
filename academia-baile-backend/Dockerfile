FROM eclipse-temurin:17
WORKDIR /app
COPY . .
RUN ./mvnw clean install -DskipTests
EXPOSE 8080
CMD ["java", "-jar", "target/academia-baile-backend-0.0.1-SNAPSHOT.jar"]
