# Usa JDK 17 como base
FROM eclipse-temurin:17

# Crea carpeta de trabajo
WORKDIR /app

# Copia el contenido del backend
COPY academia-baile-backend /app

# Da permisos y compila con Maven Wrapper (ya que usas ./mvnw)
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Expone el puerto de Spring Boot
EXPOSE 8080

# Ejecuta el .jar (ajustado al path generado)
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
