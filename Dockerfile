FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

COPY gradle ./gradle
COPY gradlew settings.gradle.kts build.gradle.kts gradle.properties ./

RUN sed -i 's/\r$//' gradlew && chmod +x gradlew
RUN ./gradlew --no-daemon dependencies

COPY src ./src

RUN ./gradlew --no-daemon installDist

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/build/install/instagallery-backend ./

EXPOSE 8080

CMD ["./bin/instagallery-backend"]
