FROM eclipse-temurin:21-jammy

WORKDIR /app

# Copy Gradle wrapper files
COPY gradlew .
RUN chmod +x ./gradlew
COPY gradle/wrapper/gradle-wrapper.properties ./gradle/wrapper/gradle-wrapper.properties
COPY gradle/wrapper/gradle-wrapper.jar ./gradle/wrapper/gradle-wrapper.jar

# Copy build configuration
COPY build.gradle .
COPY settings.gradle .

# Copy the source code
COPY src ./src

# Build the application
RUN ./gradlew build -x test


# Define the entry point to run the application
# The JAR file is located in build/libs/
ENTRYPOINT ["java", "-jar", "build/libs/reviseit-0.0.1-SNAPSHOT.jar"]
