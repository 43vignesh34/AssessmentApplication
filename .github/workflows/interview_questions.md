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

---

