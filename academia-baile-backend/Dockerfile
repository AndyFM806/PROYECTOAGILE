# Usa JDK 17 base
FROM eclipse-temurin:17-jdk-alpine

# Crea directorio de trabajo
WORKDIR /app

# Copia el pom y descarga dependencias primero (cacheo útil)
COPY pom.xml ./
COPY .mvn .mvn
COPY mvnw .
RUN ./mvnw dependency:go-offline

# Copia todo el código
COPY . .

# Compila la app
RUN ./mvnw clean package -DskipTests

# Expone el puerto 8080
EXPOSE 8080

# Ejecuta la app
CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
