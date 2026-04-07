# Centralized Observability: Loki, Prometheus, and Grafana

To build a professional observability stack, we need tools that can aggregate logs and metrics from multiple instances of our application.

## 1. The Stack Overview

| Tool | 🛠️ Purpose | 🔍 Analogy |
| :--- | :--- | :--- |
| **Prometheus** | Metric Collector | The "Health Monitor" that records heart rate and blood pressure (CPU, RAM, Request counts). |
| **Loki** | Log Aggregator | The "Library Archivist" that indexes and stores every word spoken (Application logs). |
| **Grafana** | Dashboard UI | The "Command Center" where you see all the charts and search through logs. |

---

## 2. Why Loki? (The "LogQL" Power)

Unlike Elasticsearch (which indexes every word in every log, becoming very expensive), **Loki** only indexes the "labels" (e.g., `app=assessment-app`, `env=dev`). This makes it:
- **Cost Effective**: Uses 10x less storage.
- **Fast**: Searching by time and label is nearly instant.
- **Integrated**: Seamlessly switches between a spiked metric in Prometheus and the corresponding logs in Loki.

---

## 3. How to Set It Up (Docker Compose)

To run Loki and Grafana locally, we can add the following to our `docker-compose.yml`:

```yaml
  loki:
    image: grafana/loki:latest
    ports:
      - "3100:3100"
    command: -config.file=/etc/loki/local-config.yaml

  promtail:
    image: grafana/promtail:latest
    volumes:
      - ./logs:/var/log
    command: -config.file=/etc/promtail/config.yml

  grafana:
    image: grafana/grafana:latest
    ports:
      - "3000:3000"
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
```

### The Pipeline Flow:
1. **Application** writes logs to `/logs/assessment-app.log`.
2. **Promtail** (the shipping agent) reads the file and sends it to Loki.
3. **Loki** (the warehouse) stores and indexes the logs.
4. **Grafana** (the viewer) queries Loki to show you the logs.

---

## 4. Exploring Your New Metrics

Once your app is running, you can find your custom metric `user.registrations` in:
- **Actuator JSON**: `http://localhost:8080/actuator/metrics/user.registrations`
- **Prometheus Export**: `http://localhost:8080/actuator/prometheus` (Search for `user_registrations_total`)

---

## 5. Next Steps for Professional Logs
- **Correlation IDs**: Adding a `traceId` to every log line so you can follow a single request across many microservices.
- **Alerting**: Setting up rules like "If error logs > 10 in 1 minute, send a Slack message."
