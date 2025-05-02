FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia el contenido necesario
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Da permisos y compila
RUN chmod +x mvnw
RUN ./mvnw clean install -DskipTests

# Expone el puerto del backend
EXPOSE 8080

# Ejecuta el .jar final
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
