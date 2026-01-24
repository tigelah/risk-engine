FROM eclipse-temurin:21-jre
WORKDIR /app
ARG JAR_FILE=target/risk-engine-*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8089
ENTRYPOINT ["java","-jar","/app/app.jar"]
