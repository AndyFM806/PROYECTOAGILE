FROM eclipse-temurin:17-jdk

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos necesarios
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Crear carpeta de subidas si es necesario
RUN mkdir -p ./uploads

# Dar permisos al wrapper
RUN chmod +x mvnw

# Compilar el proyecto (sin tests)
RUN ./mvnw clean package -DskipTests

# Exponer el puerto de Spring Boot
EXPOSE 8080

# Ejecutar el JAR generado
CMD ["java", "-cp", "target/backend-0.0.1-SNAPSHOT.jar", "com.academiabaile.backend.BackendApplication"]

