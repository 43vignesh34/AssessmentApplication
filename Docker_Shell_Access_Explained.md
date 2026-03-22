# Docker Shell Access & Debugging Explained

One of the most powerful features of Docker is the ability to "hop inside" a running container to see what's happening. This is essential for checking logs, verifying file paths, or testing database connectivity.

## 1. How to Access the Shell

If your container is already running (e.g., via `docker-compose up`), you can enter it using the `exec` command.

### The Standard Command:
```bash
docker exec -it <container_id_or_name> /bin/bash
```

### Breakdown:
- `exec`: Run a new command in an existing container.
- `-i` (interactive): Keep STDIN open even if not attached.
- `-t` (tty): Allocate a pseudo-TTY (makes it look like a real terminal).
- `/bin/bash`: The program you want to run (the shell).

> [!TIP]
> If `/bin/bash` doesn't work (e.g., on Alpine Linux), try `/bin/sh`.

---

## 2. Using it with Docker Compose
If you are using Docker Compose, you don't need to find the container ID. You can use the service name:

```bash
docker-compose exec app /bin/bash
```

---

## 3. Why Some Images Have "No Shell"
As you move toward production, you might hear about **Distroless** images. These are extremely tiny images that contain *only* your application and its runtime (no shell, no `ls`, no `cd`).
- **Pro**: Much more secure (hackers can't use a shell if it doesn't exist).
- **Con**: Impossible to `exec` into for debugging.

Your current image (`eclipse-temurin:17-jre`) is based on **Ubuntu**, so it **does** have a shell.

---

## 4. Adding Tools for Debugging
Sometimes the internal shell is too bare-bones. You can add tools like `curl` (to test APIs) or `vim` (to edit configs) in your `Dockerfile`:

```dockerfile
# Inside your runner stage:
RUN apt-get update && apt-get install -y curl vim && rm -rf /var/lib/apt/lists/*
```

---

## 5. Shell Form vs. Exec Form (Revisited)
In your `Dockerfile`, you have:
`ENTRYPOINT ["java", "-jar", "app.jar"]` (Exec Form)

If you change it to:
`ENTRYPOINT java -jar app.jar` (Shell Form)

The application will run *inside* a shell process. While this makes it "easier" to use environment variables in the command, it prevents the app from receiving shutdown signals correctly. **Stick to the Exec Form** and use `docker exec` when you need a shell.
