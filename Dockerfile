#Inheriting from the Base Image which has full JDK
FROM maven:3.9.6-eclipse-temurin-17 AS build

#We dont want it to be built in the root directory(Default)
#So we are creating a new directory called app and we are setting it as the working directory
WORKDIR /app


#Copying the pom.xml file FIRST to the working directory.
# This is for DOCKER LAYER CACHING: If pom.xml hasn't changed, Docker won't redownload all dependencies.
COPY pom.xml .

#Downloading the dependencies
# We use go-offline because Maven normally only downloads jars when it needs them to compile code (Lazy Approach).
# Since we haven't copied the Java source code yet, regular 'mvn package' would do nothing.
# 'go-offline' forces Maven to pull every possible dependency down so Docker can save it as a cached snapshot.
#By doing this we won't have to wait for the dependencies to be downloaded every time we build the image.
RUN mvn dependency:go-offline

#Copying the actual source code
# We do this AFTER go-offline because Java code changes frequently and we
# cannot afford to download the dependencies every time we build the image.
# Putting it here means Docker only breaks the cache (and re-compiles) when we actually touch a .java file.
#COPY source destination
COPY src ./src

# Compiling the application into a .jar file
# We use BuildKit Cache Mounts (--mount=type=cache) specifically aimed at Maven's local repository (/root/.m2).
# Even if our code or pom.xml changes heavily (breaking the standard Docker layer cache), 
# Maven will still find its previously downloaded .jar dependencies safely sitting in this persistent cache,
# ensuring lightning-fast build times.
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# =========================================================
# STAGE 2: The Runner (Production Environment)
# =========================================================

# Start a brand new, tiny image that only has the JRE (Java Runtime Environment)
# We don't need Maven or the JDK compiler anymore because the code is already compiled!
FROM eclipse-temurin:17-jre

# Set the working directory again for this new container
WORKDIR /app

# Reach back into the heavy 'build' container (Stage 1)
# Grab ONLY the compiled .jar file from the target directory and bring it into this clean container
# So that the final image is very small
# We name it app.jar for simplicity
COPY --from=build /app/target/*.jar app.jar

# Inform developers and Docker that the container will listen on port 8080
# This is just documentation; it doesn't actually open the port.
# It tells the orchestrator(like Kubernetes or Docker Compose) to forward traffic to this port.
EXPOSE 8080

# EXEC FORM vs SHELL FORM
# -----------------------
# We use the Exec Form ["java", "-jar", "app.jar"] so that the JVM runs as PID 1.
# This allows the container to receive Unix signals (like SIGTERM) directly.
#
# If we used the Shell Form (ENTRYPOINT java -jar app.jar):
# 1. Docker starts /bin/sh -c to run the command.
# 2. The Shell becomes PID 1, and Java becomes a child process.
# 3. The Shell does NOT pass signals to its children.
# 4. Result: 'docker stop' will hang for 10s and then "hard kill" the app,
#    risking data corruption and preventing graceful shutdown of DB connections.
ENTRYPOINT ["java", "-jar", "app.jar"]
