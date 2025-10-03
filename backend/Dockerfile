# Etapa 1: Build con Maven Wrapper
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copiar los archivos de Maven Wrapper primero para aprovechar la cache
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN chmod +x mvnw

# Descargar dependencias (esto se cachea si no cambian)
RUN ./mvnw dependency:go-offline -B

# Copiar el resto del código y compilar
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera para ejecutar la app
FROM eclipse-temurin:17-jdk
WORKDIR /app

# Copiar el jar compilado desde la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Render asigna el puerto en $PORT, Spring lo usará con -Dserver.port
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=$PORT -jar app.jar"]
