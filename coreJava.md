# Core Java Fundamentals

## 1. The Java Execution Stack: JVM, JRE, JDK

These are nested tools. Each one includes everything inside it.

```
┌──────────────────────────────────────┐
│               JDK                    │  ← Developer's Toolkit (compile + run)
│  ┌────────────────────────────────┐  │
│  │             JRE                │  │  ← Runtime Environment (run only)
│  │  ┌──────────────────────────┐  │  │
│  │  │          JVM             │  │  │  ← Engine (executes bytecode)
│  │  └──────────────────────────┘  │  │
│  └────────────────────────────────┘  │
└──────────────────────────────────────┘
```

### JVM — Java Virtual Machine (The Engine)
*   Reads and executes Java **Bytecode** (`.class` files).
*   Cannot read `.java` source files directly.
*   **The "Write Once, Run Anywhere" solution**: Code compiles to bytecode, not to a specific OS. Any computer with a JVM can run the same `.jar` file.
*   By itself, the JVM would crash immediately because it has no standard libraries.

### JRE — Java Runtime Environment (The Runner)
*   **JVM + Java Standard Libraries** (`java.util`, `java.lang`, `java.time`, etc.)
*   Everything needed to **run** an already-compiled Java application.
*   Does NOT include the Java compiler.
*   Used for **production servers** that only need to execute the app, not build it.
*   Example in your project: The second stage of your `Dockerfile` uses `eclipse-temurin:17-jre`.

### JDK — Java Development Kit (The Developer's Toolkit)
*   **JRE + `javac` compiler + developer tools** (`jstack`, `jconsole`, etc.)
*   Everything needed to **write, compile, and run** Java applications.
*   Used on **developer machines** and **CI/CD runners** that build your code.
*   Example in your project: The first stage of your `Dockerfile` uses `maven:3.9.6-eclipse-temurin-17` (which contains a full JDK, because Maven needs `javac`).

### Summary Table

| | JVM | JRE | JDK |
| :--- | :--- | :--- | :--- |
| **Runs Java apps** | ❌ (no std libs) | ✅ | ✅ |
| **Java Standard Libraries** | ❌ | ✅ | ✅ |
| **Java Compiler (`javac`)** | ❌ | ❌ | ✅ |
| **Use Case** | Internal engine | Production server | Developer / CI-CD |

---

## 2. The Java Compilation Flow

Code always follows this strict sequence. Compilation always comes FIRST.

```
Step 1: COMPILE (requires JDK)
  UserService.java  ─── javac ──→  UserService.class
  (Human-readable)                 (Bytecode - machine language)

Step 2: PACKAGE (Maven)
  All .class files  ─── mvn package ──→  app.jar

Step 3: RUN (requires JRE/JVM)
  java -jar app.jar  ─── JVM reads bytecode ──→  Running Application
```

*   **`javac`** lives inside the JDK. It is the compiler.
*   **`.class`** files contain Bytecode — a universal language only the JVM understands.
*   The JVM never sees your `.java` files. By the time it is involved, compilation is already complete.

---

## 3. JDK Distributions (Who Builds It)

OpenJDK is the "open-source recipe" for Java. Different companies cook that recipe into installable packages called **distributions**.

| Distribution | Provider | Best Used For |
| :--- | :--- | :--- |
| **Temurin** | Eclipse Foundation | **Industry standard.** Neutral, free, highly stable. Default choice. |
| **Amazon Corretto** | Amazon | Apps running on AWS servers. |
| **Zulu** | Azul Systems | Fast hardware support (first to support M1/M2 Macs). |
| **Oracle JDK** | Oracle | Enterprise companies needing paid 24/7 support. |

In your CI/CD pipeline (`cicd.yml`), you use `distribution: "temurin"` — the correct industry-standard choice.

---

## 4. Databases: H2 vs MySQL

| Feature | H2 | MySQL |
| :--- | :--- | :--- |
| **Location** | RAM (inside JVM process) | Disk (separate server) |
| **Data Persistence** | ❌ Erased on shutdown | ✅ Survives restarts |
| **Setup** | Just a `pom.xml` dependency | Install, configure, run server |
| **Port** | None (in-process) | 3306 |
| **Best For** | Dev, Testing, CI/CD | Staging, Production |

### Your App Right Now
*   Uses **H2** as a placeholder (configured in `application.properties`).
*   The old MySQL credentials are commented out.
*   When you want real user data to persist (a real production app), you switch to MySQL and inject credentials via AWS Elastic Beanstalk Environment Properties.
