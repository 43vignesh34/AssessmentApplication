# Maven Lifecycle: Testing vs. Packaging

In a professional CI/CD pipeline, the order of operations is critical. Maven follows a strict "Lifecycle" that moves from basic code to a finished product.

## 1. Unit Tests (`mvn test`)
*   **Goal:** Prove the **Logic** is correct.
*   **Requirement:** Only needs the compiled `.class` files.
*   **Timing:** Happens **BEFORE** the JAR is created.
*   **Why?** If a Mockito test fails, the code is logically broken. There is no point in wasting time and CPU power "packaging" a broken product. We want to **Fail Fast**.

## 2. Packaging (`mvn package`)
*   **Goal:** Create the **Finished Product** (The `.jar` file).
*   **Result:** A single file containing all your code, resources, and dependencies, ready to run on a server or inside Docker.

## 3. Integration Tests (`mvn verify`)
*   **Goal:** Prove the **Application** is healthy.
*   **Requirement:** Requires the full **JAR file** and a "live" environment (Spring context, H2 database, etc.).
*   **Timing:** Happens **AFTER** the JAR is created.
*   **Why?** We are now doing a "test drive." We want to ensure that the assembly process didn't break anything—like missing configuration files or broken database connections—that unit tests can't see.

---

### Comparison Table

| Feature | Unit Tests (`test`) | Integration Tests (`verify`) |
| :--- | :--- | :--- |
| **Focus** | Individual Methods | Full System Flow |
| **Speed** | Extremely Fast (ms) | Slower (seconds/minutes) |
| **Mocks** | Uses Mockito heavily | Uses real/embedded DBs |
| **JAR Needed?** | **No** (Test the code) | **Yes** (Test the assembly) |
