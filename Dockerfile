# Etapa de compilación: Usamos una imagen de Maven para compilar el proyecto
FROM maven:3.8.3-openjdk-17 AS build

# Copiamos los archivos de la aplicación al directorio de trabajo del contenedor
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Compilamos la aplicación con Maven
RUN mvn clean package -DskipTests

# Etapa de producción: Usamos una imagen OpenJDK para la ejecución de la aplicación
FROM openjdk:17-jdk-alpine

# Copiamos el artefacto generado en la etapa de compilación al contenedor
COPY --from=build /app/target/TODOlist-API2-1.0.0-SNAPSHOT.jar /app.jar

# Copiamos los recursos de la aplicación al contenedor
COPY src/main/resources /resources

# Expone el puerto 8080 para que la aplicación sea accesible desde fuera del contenedor
EXPOSE 8080

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "/app.jar"]

