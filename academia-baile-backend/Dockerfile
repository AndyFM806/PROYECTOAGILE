FROM eclipse-temurin:17-jdk

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven y proyecto
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .

# Descargar dependencias
RUN ./mvnw dependency:go-offline

# Copiar el código fuente después para aprovechar cache
COPY src ./src

# Dar permisos al wrapper
RUN chmod +x mvnw

# Compilar el proyecto y generar el .jar
RUN ./mvnw clean package -DskipTests

# Exponer el puerto usado por Spring Boot
EXPOSE 8080

# Ejecutar el JAR (nombre real del jar generado por Spring Boot)
CMD ["java", "-jar", "target/academia-baile-backend-0.0.1-SNAPSHOT.jar"]
