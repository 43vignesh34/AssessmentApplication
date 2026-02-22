
## Why do we copy the `pom.xml` and download dependencies FIRST?

This is one of the most important concepts in Docker: **Layer Caching**.

### How Docker Thinks

Docker builds your image line by line, from top to bottom. Once Docker finishes a line, it saves the result as a "Layer" (like a snapshot) in its cache.
The next time you build the image, Docker checks if anything on that line has changed. 
*   **If NOTHING changed:** Docker says, "Great, I already did this last time. I'll just use my saved snapshot!" (This takes 0.1 seconds).
*   **If ANYTHING changed:** Docker throws away the saved snapshot for that line AND every single line below it. It has to rebuild everything from that point downward from scratch.

### The Bad Way (Why we DON'T do this)

Imagine you copy all your code (including your `pom.xml` and all your `.java` files) at the exact same time, and then run Maven:

```dockerfile
# POOR PRACTICE
COPY . /app
RUN mvn clean package
```

1.  Today, you build this. It takes 5 minutes to download all of Spring Boot from the internet and compile your code.
2.  Tomorrow, you change exactly **one word** in a `.java` file.
3.  You run `docker build` again. Docker hits the `COPY . /app` line. It sees that one file changed. 
4.  Docker throwing away its cache. It copies everything again. 
5.  It moves to `RUN mvn clean package`. Because the cache above was broken, it has to run this from scratch. It spends 5 minutes re-downloading the entire internet again, just for a one-word typo fix.

### The Good Way (What we ARE doing)

Instead, we split it up:

```dockerfile
# BEST PRACTICE
COPY pom.xml .
RUN mvn dependency:go-offline

# THEN we copy the source code
COPY src ./src
```

1.  **Line 1 & 2:** You copy *only* the `pom.xml` file. Then you tell Maven: "Look at this `pom.xml` and download all the libraries we need from the internet, but don't compile anything yet." It takes 5 minutes to download everything, and Docker saves a snapshot of these downloaded files in its cache.
2.  Tomorrow, you change exactly **one word** in a `.java` file.
3.  You run `docker build` again. Docker hits the `COPY pom.xml .` line. It checks your `pom.xml` file on your computer. Assume you haven't added any new dependencies. Docker says, "Ah, the `pom.xml` is identical to last time! I will use my cached snapshot."
4.  It moves to `RUN mvn dependency...` and instantly uses the cache. No 5-minute internet download!
5.  Finally, it hits `COPY src ./src`. Docker sees your `.java` file changed. It breaks the cache here, copies the new code, and compiles it quickly because all the heavy libraries are already downloaded. 

**Summary:** We do things in this order so that changing your code doesn't force Docker to re-download the entire internet every single time you build.

## What do `COPY src ./src` and `RUN mvn clean package -DskipTests` do?

**What it means:**
*   `COPY src ./src`: This copies the actual Java source code from your local `src` folder into the `/app/src` folder inside the container.
*   `RUN mvn clean package -DskipTests`: This tells Maven to take that source code and compile it into an executable `.jar` file. We use `-DskipTests` to make the Docker image build much faster by skipping the unit tests. (Usually, your CI/CD pipeline verifies the tests *before* it even attempts to build the Docker image).

**Why it's needed:**
If you don't copy the source code, your container just has downloaded libraries but none of your actual application logic. If you don't run the `package` command, you won't generate the final `.jar` file that Java needs to execute when the container starts.

*(This concludes Stage 1 of the multi-stage build. Everything after this is about creating the clean runtime environment!)*

## What is Stage 2 (The Runtime Environment)?

This is the second half of a "Multi-Stage Build." 

**What it means:**
We use the `FROM` keyword a *second time* in the same file:
```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
```

**Why it's needed:**
Stage 1 (our `maven` build stage) is huge. It contains Maven, the JDK compiler, all your raw source code, and hundreds of downloaded dependencies. 

If we deployed Stage 1, our container would be 600MB+ and vulnerable (because hackers love finding build tools left inside containers).

By using `FROM` again, Docker starts a **brand new, completely empty container**. We specifically ask for the `jre` (Java Runtime Environment) version because it is tiny and only contains exactly what is needed to *run* an already-compiled Java application, not to build one.

## What is `COPY --from=build`?

**What it means:**
We use `--from=build` to tell Docker: "Don't copy this file from my local computer. Instead, go look inside the temporary container we created in Stage 1 (which we named `build`), grab the compiled `.jar` file, and bring it into this new Stage 2 container."

**Why it's needed:**
This works entirely because on Line 1 of our Dockerfile, we wrote `AS build`. We gave Stage 1 a nickname so we could refer back to it later. 

If we didn't use `--from=build`, Docker would try to find a `.jar` file on your actual Macbook, which might not exist or might be out of date. This command securely hands the baton from the heavy build environment to the lightweight runtime environment without any extra baggage.

## What are `EXPOSE` and `ENTRYPOINT`?

**1. `EXPOSE 8080`**
*   **What it means:** It documents that this container intends to listen for incoming traffic on port 8080.
*   **Why it's needed:** Technically, it doesn't *do* anything active. It's safe documentation for developers and automated systems to know which port the Spring Boot app expects to use.

**2. `ENTRYPOINT ["java", "-jar", "app.jar"]`**
*   **What it means:** This is the terminal command that executes the moment the container turns on.
*   **Why it's needed:** Without an Entrypoint or a Cmd, a container starts, realizes it has nothing to do, and immediately turns itself off. This line tells the Java Runtime Environment to execute our `.jar` application and keep the process alive.
