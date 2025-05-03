FROM eclipse-temurin:17-jdk

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos necesarios
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Dar permisos al wrapper
RUN chmod +x mvnw

# Compilar el proyecto
RUN ./mvnw clean package -DskipTests

# Exponer el puerto usado por Spring Boot
EXPOSE 8080

# Ejecutar el JAR generado
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
