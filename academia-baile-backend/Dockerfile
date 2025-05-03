FROM eclipse-temurin:17-jdk

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos necesarios (NOTA: copiamos el contenido de src, no la carpeta src)
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src/main ./src/main
COPY src/test ./src/test

# Dar permisos al wrapper
RUN chmod +x mvnw

# Compilar el proyecto
RUN ./mvnw clean package -DskipTests

# Exponer el puerto
EXPOSE 8080

# Ejecutar el jar
CMD ["java", "-jar", "target/academia-baile-backend-0.0.1-SNAPSHOT.jar"]


