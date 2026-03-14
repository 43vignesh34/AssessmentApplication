# GitHub Actions: Core Concepts & Syntax

## 1. Branch Triggers & Wildcards
You can control exactly which branch triggers a workflow.

*   **Trigger on everything:** Use the `*` wildcard to match any branch name.
    ```yaml
    on:
      push:
        branches: ["*"]
    ```
*   **The "Omission" Rule:** If you leave out the `branches` section entirely, the workflow defaults to running on **every push** to **any branch**.
*   **Main vs. Master:** `master` is the legacy name for the primary branch. In 2020, the default was changed to `main`. Including both in your list is common for backward compatibility.

## 2. The Runner: A "Blank Slate"
When a job starts, GitHub gives you a high-performance Virtual Machine (VM) running on `ubuntu-latest` (or `macos`, `windows`).

*   **Important:** This VM is completely **empty**. It does not have your code.
*   **Order of Operations:** You **MUST** run `actions/checkout` as your very first step. If you try to setup Java or Maven before checking out, the job will fail because it won't find your `pom.xml` or source code.

## 3. Job-Level Scoping (`runs-on`)
The `runs-on` setting lives **inside** the job ID, not at the top of the file. This allows you to run different jobs on different operating systems within the same workflow.

## 4. YAML Syntax Precision
YAML is highly sensitive to spaces and indentation.

*   **The Dash-Space Rule:** In a list of steps, you must have a space after the dash. 
    *   ❌ `-name: Step Name` (Wrong: treated as one word)
    *   ✅ `- name: Step Name` (Correct: defines a list item)
*   **Indentation matters:** Wrong indentation will cause a "Schema Validation Error," meaning GitHub can't understand who "owns" which command.

## 5. Docker Hub Login: Authentication & Ownership
Even if you build a perfect Docker image on the runner, you need permission to upload it to the cloud.

*   **Ownership:** Docker Hub needs to prove that **you** are the owner of the account before letting you "push" an image. This prevents strangers from overwriting your code.
*   **The "Hand-off":** The GitHub Action runner is temporary. If you don't "push" your image to Docker Hub immediately, the image disappears when the job finishes.
*   **Rate Limits:** Logged-in users get much higher download (pull) limits than anonymous users, preventing your pipeline from being blocked during busy periods.
*   **Tagging Requirements:** You **cannot** push an image named just `app-name`. You **must** prefix it with your Docker Hub username (e.g., `43vignesh34/app-name`). If the username is missing, Docker Hub will reject the push.


