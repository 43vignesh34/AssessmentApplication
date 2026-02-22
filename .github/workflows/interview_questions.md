# CI/CD Pipeline Interview Questions

Based on the pipeline you just built, here are the most likely interview questions you'll face.

## 1. "Walk me through your CI/CD pipeline."
**Your Answer:**
> "I built a GitHub Actions pipeline for my Spring Boot application. It has two main stages:
> 1.  **CI (Continuous Integration):** Triggered on every push. It checks out the code, sets up Java 17, compiles the app using Maven (skipping tests for speed), runs Unit & Integration tests using `mvn verify`, and finally packages the application as a JAR file.
> 2.  **CD (Continuous Deployment):** If the CI stage passes, it downloads the built JAR artifact and deploys it to AWS Elastic Beanstalk using secure credentials stored in GitHub Secrets."

## 2. "Why did you skip tests during the build step (`-DskipTests`)?"
**Your Answer:**
> "I separated the 'Build' and 'Test' phases for faster feedback and better debugging.
> *   **Fail Fast:** If the code doesn't compile, I want to know immediately without waiting for tests.
> *   **Clarity:** If the 'Build' step fails, I know it's a syntax or dependency issue. If the 'Test' step fails, I know it's a logic issue."

## 3. "What is `ubuntu-latest` and why do you use it?"
**Your Answer:**
> "It's the runner (virtual machine) provided by GitHub. I use it to ensure a **clean, isolated environment** for every build. This eliminates the 'it works on my machine' problem because every build starts from a fresh OS state with no pre-installed dependencies or cached files."

## 4. "How do you pass the built application from the Build job to the Deploy job?"
**Your Answer:**
> "I use **GitHub Actions Artifacts**. Since runners are ephemeral (destroyed after each job), the JAR file built in the first job would be lost.
> *   I use `actions/upload-artifact` in the Build job to save the JAR.
> *   I use `actions/download-artifact` in the Deploy job to retrieve it."

## 5. "What is the difference between `mvn package` and `mvn verify`?"
**Your Answer:**
> *   **`mvn package`**: Compiles the code and packages it into a JAR file (usually runs unit tests too, unless skipped).
> *   **`mvn verify`**: Runs the `package` phase PLUS the `integration-test` phase and checks the results. I use `verify` in CI to ensure **both** unit tests and integration tests pass before deployment."

## 6. "How do you handle sensitive data like AWS Keys?"
**Your Answer:**
> "I never hardcode secrets in the YAML file. I use **GitHub Repository Secrets** to store credentials like `AWS_ACCESS_KEY_ID`. In the workflow, I reference them using `${{ secrets.AWS_ACCESS_KEY_ID }}`, so they are injected as environment variables only during execution and are redacted in the logs."

## 7. "Do I need to memorize pipeline syntax?"
**The Reality:**
> *   **No:** Most interviewers (even Seniors) don't memorize every line of YAML syntax.
> *   **Yes:** You need to know the **structure** and **modules** (Triggers -> Jobs -> Steps -> Actions).
> *   **Strategy:** "I start with a template or documentation, but I customize the logic. I know I need a Checkout step, a Setup Java step, and a Build step. The exact syntax like `uses: actions/checkout@v4` I verify from the marketplace."

## Dockerfile Mnemonic: The 5 C's

**"Crazy Cats Can't Catch Cars"** (or "Containers Cache Code, Creating Commands")

Here is the breakdown of each "C", the commands involved, and what happens if you skip it.

### 1. Context (Crazy / Containers)
**Goal:** Set the stage and the working directory.
*   **Commands:**
    *   `FROM maven:3.9.6-eclipse-temurin-17 AS build` (or similar base image)
    *   `WORKDIR /app`
*   **What happens without it?**
    *   Without `FROM`: Docker doesn't know what starting operating system or tools to use. The build fails immediately.
    *   Without `WORKDIR`: Commands run in the root (`/`) directory by default. This makes the container messy and risks overwriting important system files.

### 2. Cache Dependencies (Cats / Cache)
**Goal:** Be lazy. Grab the recipe and ingredients first to save time on future builds.
*   **Commands:**
    *   `COPY pom.xml .`
    *   `RUN mvn dependency:go-offline`
*   **What happens without it?**
    *   If you copy your source code *before* downloading dependencies, any tiny change to a `.java` file invalidates Docker's cache.
    *   Maven will have to re-download all the dependencies from the internet every single time you change a line of code, turning a 5-second build into a 5-minute build.

### 3. Compile (Can't / Code)
**Goal:** Bring the logic and build the final product.
*   **Commands:**
    *   `COPY src ./src`
    *   `RUN mvn clean package -DskipTests`
*   **What happens without it?**
    *   Without `COPY src`: Your actual application code never makes it into the container.
    *   Without `RUN mvn package`: The `.jar` (executable) file is never created, so there is nothing to run.

### 4. Clean Runtime (Catch / Creating)
**Goal:** Drop the heavy build tools and create a secure, lightweight final container.
*   **Commands:**
    *   `FROM eclipse-temurin:17-jre` (Notice it's `jre`, not the full JDK/Maven)
    *   `WORKDIR /app`
    *   `COPY --from=build /app/target/*.jar app.jar`
*   **What happens without it?**
    *   If you don't do this second stage, your final Docker image will contain the entire Maven installation, the source code, and all the downloaded dependencies.
    *   The image size will be bloated (e.g., 600MB+ instead of ~200MB).
    *   It's less secure because attackers have access to build tools if they compromise the container.

### 5. Command (Cars / Commands)
**Goal:** Start the engine and tell Docker what port to look at.
*   **Commands:**
    *   `EXPOSE 8080`
    *   `ENTRYPOINT ["java", "-jar", "app.jar"]`
*   **What happens without it?**
    *   Without `EXPOSE`: This is mostly for documentation, but without it, developers have a harder time knowing which port the container expects traffic on.
    *   Without `ENTRYPOINT`: The container starts up, doesn't know what process it's supposed to run, and immediately exits.

## What is `maven:3.9.6-eclipse-temurin-17`?

This string is a **Docker Image Tag**. It tells Docker exactly which pre-built starting environment to download from Docker Hub and use as the foundation for your container. 

Let's break it down into three parts:

### 1. The Tool (`maven`)
*   **What it is:** This is the name of the base image on Docker Hub.
*   **What it means:** It tells Docker, "I want an environment that has Apache Maven pre-installed."

### 2. The Tool Version (`3.9.6`)
*   **What it is:** This specifies the exact version of Maven you want. 
*   **Why it's important:** If you just used `maven:latest`, your build might break randomly next year when Maven releases version 4.0 and changes how things work. Pinning it to a specific version (`3.9.6`) ensures your builds are **reproducible** and stable over time.

### 3. The Operating System & Underlying Language (`eclipse-temurin-17`)
*   **What it is:** The flavor of Java and OS that Maven sits on top of. 
    *   **Eclipse Temurin:** This is a popular, open-source, and highly stable distribution of the Java Development Kit (JDK) maintained by the Eclipse Foundation. It's essentially the industry standard replacement for the official Oracle JDK (which now requires paid licenses for production use).
    *   **17:** The version of Java (Java 17).
*   **Why it's important:** Maven needs Java to run, and it needs an operating system to live in. This specific tag ensures your container uses Java 17, which matches the `<java.version>17</java.version>` specified in your application's `pom.xml`. The underlying OS is usually a flavor of Linux (like Ubuntu or Debian).

### Summary
When you write `FROM maven:3.9.6-eclipse-temurin-17 AS build`, you are telling Docker: 

*"Go to Docker Hub and give me a Linux machine that already has Java 17 (specifically the Temurin distribution) and Maven 3.9.6 installed, so I can use it to compile my code."*

## What is the `FROM` Keyword?

The `FROM` instruction is the fundamental starting point of almost every Dockerfile. It tells Docker *what existing image to use as a foundation* for the new image you are building. It's essentially saying: **"Start with this specific environment."**

### Key Concepts

1.  **The Foundation (Base Image):** A Docker image is built in layers. The `FROM` instruction defines the very first layer, known as the **base image**. Everything you add or change in your Dockerfile (using `COPY`, `RUN`, etc.) is built *on top* of this base image.
2.  **It Usually Comes First:** A valid Dockerfile must start with a `FROM` instruction (with very rare exceptions, like `ARG` preceding it).
3.  **Choosing the Right Base:** The base image dictates what operating system (e.g., Alpine Linux, Ubuntu) and what pre-installed software (e.g., Java, Python, Node.js) your container will have.
    *   **Heavy Base (e.g., `FROM ubuntu:latest`):** Gives you a full OS with lots of tools, but results in a large, slower-to-download image.
    *   **Lightweight Base (e.g., `FROM alpine:latest`):** A tiny, stripped-down Linux distribution. It creates very small, secure images but might lack some common tools you are used to.
    *   **Tool-Specific Base (e.g., `FROM eclipse-temurin:17-jre`):** Often the best choice. It provides a lightweight OS *plus* exactly the tool you need (like a Java Runtime), without unnecessary extras.

### Example in Context

When you see:
```dockerfile
FROM eclipse-temurin:17-jre
```
You are telling Docker: "Don't build an operating system from scratch. Go get the pre-built `eclipse-temurin` image on version `17-jre`, which already has a lean Linux OS and Java 17 ready to go, and use that as my foundation."

### Multi-Stage Builds (Advanced)

In a multi-stage Dockerfile (the industry standard for compiled languages like Java or Go), you will see the `FROM` keyword used *multiple times*. Each `FROM` instruction starts a new, isolated stage. This allows you to use a heavy base image with all the build tools (like Maven) to compile the code in one stage, and then copy only the final, compiled application into a fresh, lightweight base image in the next stage.
