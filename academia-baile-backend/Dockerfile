# Usa JDK 17 como base
FROM eclipse-temurin:17

# Crea carpeta de trabajo
WORKDIR /app

# Copia el contenido actual (ya estás dentro de academia-baile-backend)
COPY . .

# Da permisos y compila
RUN chmod +x ./mvnw && ./mvnw clean package -DskipTests

# Expone el puerto 8080
EXPOSE 8080

# Ejecuta el jar generado
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
