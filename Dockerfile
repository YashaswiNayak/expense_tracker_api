FROM gradle:8-jdk21 AS builder
WORKDIR /app

# Copy only dependency files first (cached layer)
COPY build.gradle settings.gradle ./
RUN gradle dependencies --no-daemon || true

# Now copy source and build
COPY . .
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
