# Etapa 1: compilar el proyecto con Maven y Java 24
FROM maven:3.9-eclipse-temurin-24 AS build
WORKDIR /app

# Primero solo el pom para aprovechar la cache de dependencias entre builds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# Etapa 2: imagen liviana solo con el JRE y el jar
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app

# No correr la aplicacion como root dentro del contenedor
RUN addgroup -S brasa && adduser -S brasa -G brasa
USER brasa

COPY --from=build /app/target/restaurante-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
