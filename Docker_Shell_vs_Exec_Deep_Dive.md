# Deep Dive: Shell vs. Exec Form

This is one of the "invisible" parts of Docker that separate junior engineers from seniors. It’s all about how the Linux Kernel handles **Processes (PID 1)** and **Signals**.

---

## 1. Syntax Comparison

### **Shell Form**
```dockerfile
ENTRYPOINT java -jar app.jar
```
- Looks like a standard command.
- Docker internally runs this as: `/bin/sh -c "java -jar app.jar"`

### **Exec Form (Preferred)**
```dockerfile
ENTRYPOINT ["java", "-jar", "app.jar"]
```
- Uses a JSON array (brackets and quotes are required).
- Docker runs this **directly** as the command. No shell involved.

---

## 2. The "PID 1" Problem (The Kernel Mystery)

In every Linux environment, **Process ID 1 (PID 1)** is special. It is the "Init" process. It is the first thing that starts, and it has special responsibilities:
1.  **Reaping Zombies**: It must clean up dead child processes.
2.  **Handling Signals**: It is the **only** process that receives signals from host tools (like `docker stop`).

### **In Shell Form:**
1.  The **Shell** starts and becomes **PID 1**.
2.  The Shell then starts **Java** as its **Child Process (PID 2+).**
3.  **The Crisis**: Standard shells (like `sh`) do **NOT** forward signals to their children.

### **In Exec Form:**
1.  **Java** starts and immediately becomes **PID 1**.
2.  There is no middle-man (no shell).
3.  **The Result**: Java receives signals directly from the Kernel.

---

## 3. Graceful Shutdown vs. "The Hammer"

Imagine you run `docker stop my-app`. Docker sends a **SIGTERM** signal (a polite "Please pack up and leave" request).

### **Scenario A: Shell Form**
- Docker sends SIGTERM to the Shell (PID 1).
- The Shell ignores it (it doesn't care).
- Your Java app **never hears** the signal. It keeps answering requests and holding DB connections open.
- 10 seconds later, Docker gets annoyed and sends **SIGKILL** (The Hammer).
- **Result**: Your app is killed instantly. Data might be corrupted, and DB connections leak.

### **Scenario B: Exec Form**
- Docker sends SIGTERM directly to Java (PID 1).
- Java (via Spring Boot) catches the signal.
- Spring Boot starts its graceful shutdown: stops taking new requests, finishes existing ones, and closes the database pool safely.
- **Result**: The app exits cleanly.

---

## 4. Environment Variables

There is one "benefit" to Shell form: it automatically expands variables like `$JAVA_OPTS`. 

If you use **Exec Form**, `$JAVA_OPTS` will be treated as literal text. To get the best of both worlds (Shell behavior + Signal forwarding), we do this:

```dockerfile
ENTRYPOINT ["/bin/sh", "-c", "java -jar app.jar"]
```

---

## Summary Table

| Feature | Shell Form | Exec Form |
| :--- | :--- | :--- |
| **Command** | `sh -c "cmd"` | `cmd` |
| **PID 1** | The Shell | Your App |
| **Signals (SIGTERM)** | Ignored (Hangs for 10s) | **Received (Instant & Clean)** |
| **Env Vars** | Automatic | Not supported alone |
| **Standard** | Deprecated for production | **Industry Standard** |
