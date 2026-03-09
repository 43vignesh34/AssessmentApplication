# Maven Cheat Sheet for SDEs

Maven is a build automation and dependency management tool primarily used for Java projects. It uses an XML file (`pom.xml`) to describe the software project being built, its dependencies on other external modules and components, and the build order.

## 1. The Maven Build Lifecycle
When you run a Maven command, it progresses through a strict sequence of phases. If you call a later phase, it automatically runs every phase before it.

The main sequence (in order):
1.  **`validate`**: Checks if the project is correct and all necessary information is available.
2.  **`compile`**: Compiles the source code (`.java` -> `.class`).
3.  **`test`**: Runs the unit tests using a suitable testing framework (like JUnit).
4.  **`package`**: Takes the compiled code and packages it into its distributable format (like a `.jar` or `.war`).
5.  **`verify`**: Runs integration tests to ensure quality criteria are met.
6.  **`install`**: Installs the package into your *local* repository (your Macbook's `~/.m2` folder) for use as a dependency in other projects locally.
7.  **`deploy`**: Copies the final package to a *remote* repository (like Nexus or Artifactory) for sharing with other developers and projects.

## 2. The Core Commands

### `mvn clean`
*   **What it does:** Deletes the `target/` directory entirely.
*   **Why use it:** Gets rid of old, previously compiled files to ensure you are starting from a completely fresh slate. It is almost always used in combination with other commands (e.g., `mvn clean package`).

### `mvn compile`
*   **What it does:** Compiles only your main source code (`src/main/java`).
*   **Why use it:** A quick check to see if your code has compilation errors without running tests or building the heavy `.jar` file.

### `mvn test`
*   **What it does:** Compiles your code AND runs your unit/integration tests located in `src/test/java`.
*   **Why use it:** To verify your tests pass before pushing code to GitHub.

### `mvn package`
*   **What it does:** Compiles, tests, and bundles the application into its final `.jar` (or `.war`) artifact inside the `target/` directory.
*   **Why use it:** You want the actual executable file that you will run on a server or place inside a Docker container.

### `mvn install`
*   **What it does:** Does everything `package` does, PLUS it copies that `.jar` into your hidden `~/.m2/repository` folder on your laptop.
*   **Why use it:** If you are building an internal library (like `my-company-utils.jar`) and you want another Java project on your same laptop to be able to use it as a dependency in its `pom.xml`.

---

## 3. Essential Flags & Modifiers

### `-DskipTests` (The "I'm in a hurry" flag)
*   **Command:** `mvn clean package -DskipTests`
*   **What it does:** Compiles the test code, but *does not execute* the tests.
*   **Why use it:** You are writing a `Dockerfile` and just want the `.jar` built quickly, relying on the CI/CD pipeline to have already verified the tests.

### `-Dmaven.test.skip=true` (The "Skip Everything Testing" flag)
*   **Command:** `mvn clean package -Dmaven.test.skip=true`
*   **What it does:** Completely skips compiling the test code AND skips executing the tests. It's even faster than `-DskipTests`.

### `-o` or `--offline`
*   **Command:** `mvn clean package -o`
*   **What it does:** Forces Maven to completely ignore the internet. It will only use dependencies that have already been downloaded to your local `.m2` folder.
*   **Why use it:** You are working on an airplane with no WiFi, or GitHub/Maven Central goes down.

---

## 4. Advanced/Diagnostic Commands (Interview Material)

### `mvn dependency:tree` (The Lifesaver)
*   **What it does:** Prints a visual tree structure of every single dependency your project uses in the terminal, including transitive dependencies (the dependencies of your dependencies).
*   **Why use it:** **Dependency Conflicts!** If your app crashes saying "Method Not Found" in Log4J, you use this command to find out which library secretly imported an ancient, broken version of Log4J so you can exclude it.

### `mvn dependency:go-offline` (The Docker Hack)
*   **What it does:** Analyzes the `pom.xml` and downloads every single dependency, plugin, and report needed for the project from the internet immediately, without actually compiling any code.
*   **Why use it:** Used exclusively inside `Dockerfile`s to trick Docker into caching all massive library downloads into a dedicated Docker image layer *before* copying the frequently-changed Java source code. This saves 5+ minutes on every subsequent `docker build`.

## 5. What is the Maven Wrapper (`mvnw`)?
If you see `./mvnw clean package` instead of `mvn clean package`, it means the project is using the Maven Wrapper.
*   **The Problem:** You have Maven 3.1 installed on your Macbook. The project requires Maven 3.9. Your build fails.
*   **The Wrapper Solution:** The repository contains a script (`mvnw`) and a tiny `.mvn` folder. When you run `./mvnw package`, the script downloads the exact version of Maven the project expects, installs it into a hidden folder, and runs the build with it. It guarantees perfectly consistent builds across every developer's machine without forcing them to manually install specific Maven versions globally on their OS.

## 6. Core Concepts: `.class` vs `.jar`

Understanding the difference between these two file types is fundamental to Java and Maven.

### `.class` (Compiled Java Bytecode)
*   **What it is:** When you run `mvn compile`, the Java compiler (`javac`) reads your human-readable `.java` source code files and translates them, one-by-one, into `.class` files.
*   **What's inside:** It contains Java bytecode. This is intermediate code that is not meant for a human to read, nor is it meant for your computer's native processor (CPU) to execute directly. 
*   **How it runs:** A `.class` file can *only* be executed by a Java Virtual Machine (JVM). The JVM translates this bytecode into the machine language your specific operating system (Mac, Windows, Linux) understands.
*   **Analogy:** A single translated page of a book.

### `.jar` (Java ARchive)
*   **What it is:** When you run `mvn package`, Maven takes all of your hundreds of compiled `.class` files, all of your application's resources (like `application.properties`, images, HTML templates), and bundles them together into a single, compressed file using the ZIP format. This single file gets the `.jar` extension.
*   **What's inside:** It is literally just a ZIP file. If you run `unzip myapp.jar`, you will see all of your `.class` files organized into folders matching your package names (e.g., `com/example/app/Main.class`).
*   **How it runs:** You can run it directly with the `java -jar myapp.jar` command.
*   **Analogy:** The entire, bound book containing all the translated pages and illustrations, ready to be distributed.

**Why we need `.jar` files:**
You wouldn't want to email someone 500 individual `.class` files arranged in complex folder structures to share your application. A `.jar` provides a convenient, single-file distribution mechanism. Spring Boot takes this a step further by creating "Fat JARs" that also include the embedded Tomcat web server and all your external `.jar` dependencies (like Hibernate or MySQL drivers) inside your main `.jar`.
