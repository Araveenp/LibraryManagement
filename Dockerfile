# Multi-stage build: build WAR with Maven, then run on Tomcat

# -------- Build stage --------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml ./
# Pre-fetch dependencies to speed up repeated builds
RUN mvn -q -e -DskipTests dependency:go-offline || true
COPY . ./
RUN mvn -q -DskipTests clean package

# -------- Runtime stage --------
FROM tomcat:9.0-jdk21-temurin
# Avoid default ROOT app
RUN rm -rf /usr/local/tomcat/webapps/*
# Copy our WAR as ROOT.war so app is served at /
COPY --from=build /app/target/library-management-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
# Expose port 8080 (Render will map it automatically)
EXPOSE 8080
CMD ["catalina.sh", "run"]
