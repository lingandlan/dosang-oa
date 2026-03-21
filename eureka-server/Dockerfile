# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy parent pom.xml first and install to local repo
COPY pom.xml ./
RUN mvn install -N -B

# Copy service pom.xml and build
COPY eureka-server/pom.xml ./
RUN mvn dependency:go-offline -B

COPY eureka-server/src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=builder /build/target/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
