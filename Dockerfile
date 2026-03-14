FROM eclipse-temurin:17-jdk-alpine

# Instalar zona horaria
RUN apk add --no-cache tzdata

# Zona horaria Ecuador
ENV TZ=America/Guayaquil

WORKDIR /app
COPY target/ms-clientes-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]
