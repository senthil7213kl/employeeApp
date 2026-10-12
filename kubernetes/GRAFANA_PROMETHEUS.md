# Grafana & Prometheus Monitoring Stack

Complete monitoring and metrics solution for the Employee Management Application using **Prometheus** for metrics collection and **Grafana** for visualization.

## Table of Contents

1. [Architecture Overview](#1-architecture-overview)
2. [Components](#2-components)
3. [Prerequisites](#3-prerequisites)
4. [Deployment](#4-deployment)
5. [Accessing the Monitoring Stack](#5-accessing-the-monitoring-stack)
6. [Spring Boot Actuator Configuration](#6-spring-boot-actuator-configuration)
7. [Prometheus Configuration](#7-prometheus-configuration)
8. [Grafana Configuration](#8-grafana-configuration)
9. [Dashboard Overview](#9-dashboard-overview)
10. [PromQL Queries Reference](#10-promql-queries-reference)
11. [Alerting](#11-alerting)
12. [Accessing via Ingress](#12-accessing-via-ingress)
13. [Troubleshooting](#13-troubleshooting)
14. [Resource Requirements](#14-resource-requirements)
15. [Quick Reference](#15-quick-reference)

---

## 1. Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Kubernetes Cluster                       │
│                     (employeeapp ns)                        │
│                                                             │
│  ┌──────────────┐    /actuator/prometheus    ┌──────────┐  │
│  │  Employeeapp │ ──────────────────────────→│Prometheus│  │
│  │   (3 pods)   │     Scrape every 10s       │  :9090   │  │
│  └──────────────┘                            └────┬─────┘  │
│                                                   │        │
│                                    HTTP Query     │        │
│                                                   ↓        │
│  ┌──────────────┐    Datasource API      ┌──────────┐     │
│  │   Grafana    │ ←───────────────────── │Prometheus│     │
│  │   :3000      │    (auto-provisioned)   │  :9090   │     │
│  └──────────────┘                         └──────────┘     │
│       │                                                   │
│       ↓                                                    │
│  ┌──────────────┐                                         │
│  │  Dashboards  │  JVM, HTTP, GC, DB, K8s Resources       │
│  │ (auto-loaded)│                                         │
│  └──────────────┘                                         │
└─────────────────────────────────────────────────────────────┘
```

### Data Flow

1. **Spring Boot Actuator** exposes metrics at `/actuator/prometheus` in Prometheus format
2. **Prometheus** scrapes each employeeapp pod every 10 seconds using Kubernetes service discovery
3. **Grafana** queries Prometheus via its HTTP API to render dashboards
4. **Dashboards** are auto-loaded from ConfigMaps on Grafana startup

---

## 2. Components

| Component | Version | Port | Purpose |
|---|---|---|---|
| **Prometheus** | v2.51.0 | 9090 | Time-series metrics collection and storage |
| **Grafana** | 10.4.0 | 3000 | Metrics visualization and dashboards |
| **Micrometer** | (Spring Boot) | — | Metrics registry bridge (Prometheus format) |
| **Spring Boot Actuator** | (Spring Boot) | 8080 | Exposes JVM, HTTP, DB, and custom metrics |

### File Structure

```
kubernetes/
├── prometheus-configmap.yaml      # Prometheus scrape configuration
├── prometheus-rbac.yaml           # ServiceAccount, Role, RoleBinding
├── prometheus-deployment.yaml     # Prometheus Deployment
├── prometheus-service.yaml        # Prometheus ClusterIP Service
├── grafana-configmap.yaml         # Grafana config + admin secret
├── grafana-deployment.yaml        # Grafana Deployment
├── grafana-service.yaml           # Grafana ClusterIP Service
├── grafana-pvc.yaml               # Persistent storage for Grafana
├── grafana-datasources.yaml       # Prometheus datasource provisioning
├── grafana-dashboards-configmap.yaml  # Dashboard provider config
└── grafana-dashboards/
    └── spring-boot-dashboard.json # Spring Boot metrics dashboard
```

---

## 3. Prerequisites

- Kubernetes cluster running (Minikube, Kind, EKS, GKE, etc.)
- `kubectl` configured and pointing to your cluster
- Employeeapp already deployed in the `employeeapp` namespace
- Ingress controller installed (optional, for external access)

---

## 4. Deployment

### 4.1 Deploy the Full Monitoring Stack

```powershell
# Apply all monitoring manifests in the correct order
kubectl apply -f kubernetes/prometheus-configmap.yaml
kubectl apply -f kubernetes/prometheus-rbac.yaml
kubectl apply -f kubernetes/prometheus-deployment.yaml
kubectl apply -f kubernetes/prometheus-service.yaml

kubectl apply -f kubernetes/grafana-configmap.yaml
kubectl apply -f kubernetes/grafana-pvc.yaml
kubectl apply -f kubernetes/grafana-datasources.yaml
kubectl apply -f kubernetes/grafana-dashboards-configmap.yaml
kubectl apply -f kubernetes/grafana-deployment.yaml
kubectl apply -f kubernetes/grafana-service.yaml
```

### 4.2 One-Liner Deployment

```powershell
# Deploy all monitoring components at once
kubectl apply -f kubernetes/prometheus-*.yaml -f kubernetes/grafana-*.yaml
```

### 4.3 Verify Deployment

```powershell
# Check all monitoring pods are running
kubectl get pods -n employeeapp | Select-String "prometheus|grafana"

# Expected output:
# prometheus-xxxxx-xxxxx   1/1   Running   0   2m
# grafana-xxxxx-xxxxx      1/1   Running   0   2m

# Check services
kubectl get svc -n employeeapp | Select-String "prometheus|grafana"

# Verify Prometheus targets
kubectl port-forward -n employeeapp svc/prometheus 9090:9090
# Then visit: http://localhost:9090/targets

# Verify Grafana dashboards
kubectl port-forward -n employeeapp svc/grafana 3000:3000
# Then visit: http://localhost:3000
```

---

## 5. Accessing the Monitoring Stack

### 5.1 Port Forwarding (Quick Access)

```powershell
# Prometheus
kubectl port-forward -n employeeapp svc/prometheus 9090:9090
# Open: http://localhost:9090

# Grafana
kubectl port-forward -n employeeapp svc/grafana 3000:3000
# Open: http://localhost:3000
```

### 5.2 Grafana Login

| Field | Value |
|---|---|
| **URL** | `http://localhost:3000` (via port-forward) |
| **Username** | `admin` |
| **Password** | `admin` |

> **Security note:** Change the default password in production. Edit `grafana-configmap.yaml` and update the `grafana-secret` Secret.

### 5.3 Prometheus UI

| Field | Value |
|---|---|
| **URL** | `http://localhost:9090` (via port-forward) |
| **Targets page** | `http://localhost:9090/targets` |
| **Query page** | `http://localhost:9090/graph` |

---

## 6. Spring Boot Actuator Configuration

### 6.1 Dependencies Added

The following dependency was added to `pom.xml`:

```xml
<!-- Micrometer Prometheus Registry for metrics export -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### 6.2 Actuator Properties

Both `application.properties` and `application-prod.properties` now include:

```properties
# Actuator for health checks and Prometheus metrics
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoint.health.show-details=always
management.endpoint.health.show-components=always
management.metrics.tags.application=${spring.application.name}
```

### 6.3 Metrics Endpoint

When the application is running, metrics are available at:

```
http://<pod-ip>:8080/actuator/prometheus
```

Example output:

```
# HELP jvm_memory_used_bytes The used bytes of a given Java memory area
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Eden Space",application="employeeapp",} 1.234567E7
# HELP http_server_requests_seconds HTTP server requests timings
# TYPE http_server_requests_seconds histogram
http_server_requests_seconds_sum{method="GET",uri="/api/employees",status="200",} 0.045
```

### 6.4 Available Metrics Categories

| Category | Metrics Prefix | Description |
|---|---|---|
| **JVM Memory** | `jvm_memory_*` | Heap/non-heap usage, pools, max |
| **JVM Threads** | `jvm_threads_*` | Live, peak, daemon threads |
| **JVM GC** | `jvm_gc_*` | Pause times, collection counts |
| **HTTP Server** | `http_server_requests_*` | Request count, duration, status |
| **HikariCP** | `hikaricp_*` | Connection pool stats |
| **JPA** | `jpa_*` | Hibernate statistics |
| **Process** | `process_*` | CPU, uptime, file descriptors |
| **Logback** | `logback_*` | Log event counts |

---

## 7. Prometheus Configuration

### 7.1 Scrape Configuration

Prometheus is configured via `prometheus-configmap.yaml`:

```yaml
scrape_configs:
  - job_name: 'employeeapp'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
    kubernetes_sd_configs:
      - role: pod
        namespaces:
          own_namespace: true
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_app_kubernetes_io_name]
        action: keep
        regex: employeeapp
```

### 7.2 Key Settings

| Setting | Value | Purpose |
|---|---|---|
| `scrape_interval` | 15s | Global default scrape interval |
| `evaluation_interval` | 15s | How often rules are evaluated |
| `metrics_path` | `/actuator/prometheus` | Spring Boot metrics endpoint |
| `scrape_interval` (job) | 10s | Per-job override for employeeapp |
| `retention.time` | 15d | How long metrics are stored |
| `retention.size` | 5GB | Max storage size |
| `web.enable-lifecycle` | true | Allow config reload via API |

### 7.3 Service Discovery

Prometheus uses **Kubernetes native service discovery** to automatically find employeeapp pods. When pods scale up/down via HPA, Prometheus automatically discovers new targets without configuration changes.

### 7.4 Reload Configuration

```powershell
# Reload Prometheus config without restart
kubectl exec -n employeeapp deployment/prometheus -- curl -X POST http://localhost:9090/-/reload
```

---

## 8. Grafana Configuration

### 8.1 Auto-Provisioning

Grafana is configured to auto-provision on startup:

**Datasources** (from `grafana-datasources.yaml`):
- Prometheus datasource pointing to `http://prometheus.employeeapp.svc.cluster.local:9090`

**Dashboards** (from `grafana-dashboards-configmap.yaml`):
- Dashboard provider watches `/var/lib/grafana/dashboards/` for JSON files
- `spring-boot-dashboard.json` is auto-loaded

### 8.2 Persistent Storage

Grafana data (dashboards, users, preferences) is stored on a 2Gi PVC (`grafana-pvc`), surviving pod restarts.

### 8.3 Installed Plugins

- `grafana-piechart-panel` — Pie chart visualization panel

---

## 9. Dashboard Overview

The **Spring Boot - Employeeapp** dashboard includes the following sections:

### 9.1 Application Overview (Stat Panels)

| Panel | Metric | Description |
|---|---|---|
| **Pods Running** | `sum(up{job="employeeapp"})` | Number of healthy pods |
| **HTTP Requests/sec** | `rate(http_server_requests_seconds_count[1m])` | Current request rate |
| **HTTP Response Time (p95)** | `histogram_quantile(0.95, ...)` | 95th percentile response time |
| **HTTP 5xx Errors/sec** | `rate(...status=~"5.."[1m])` | Server error rate |

### 9.2 JVM Memory (Time Series)

| Panel | Metrics | Description |
|---|---|---|
| **JVM Heap Memory** | Eden, Old Gen, Max | Heap usage per pod |
| **JVM Non-Heap Memory** | Metaspace, Compressed Class Space | Non-heap usage per pod |

### 9.3 JVM CPU & Threads (Time Series)

| Panel | Metrics | Description |
|---|---|---|
| **JVM CPU Usage** | `rate(process_cpu_seconds_total[1m])` | CPU utilization per pod |
| **JVM Threads** | Live, Peak, Daemon | Thread counts per pod |

### 9.4 Garbage Collection (Time Series)

| Panel | Metrics | Description |
|---|---|---|
| **GC Frequency** | `rate(jvm_gc_pause_seconds_count[5m])` | GC events per second |
| **GC Pause Duration** | Avg pause time | Average GC pause per action |

### 9.5 HTTP Requests Detail (Time Series)

| Panel | Metrics | Description |
|---|---|---|
| **Requests by Method & Status** | Grouped by method + status | Request distribution |
| **Response Time Percentiles** | p50, p95, p99 by method | Latency breakdown |

### 9.6 Database & Connections (Time Series)

| Panel | Metrics | Description |
|---|---|---|
| **HikariCP Connection Pool** | Active, Idle, Pending | Connection pool state |
| **DB Connection Usage Time** | Avg usage time | How long connections are held |

### 9.7 Kubernetes Resources (Time Series)

| Panel | Metrics | Description |
|---|---|---|
| **Container Memory Usage** | `container_memory_working_set_bytes` | Actual memory per pod |
| **Container CPU Usage** | `container_cpu_usage_seconds_total` | CPU cores per pod |

---

## 10. PromQL Queries Reference

### 10.1 Application Health

```promql
# Number of healthy pods
up{job="employeeapp"}

# Total pods up
sum(up{job="employeeapp"})

# Pod uptime in seconds
process_uptime_seconds{job="employeeapp"}
```

### 10.2 HTTP Metrics

```promql
# Request rate per second
rate(http_server_requests_seconds_count{job="employeeapp"}[1m])

# Request rate by method and status
sum(rate(http_server_requests_seconds_count{job="employeeapp"}[1m])) by (method, status)

# 95th percentile response time
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job="employeeapp"}[5m])) by (le))

# 99th percentile response time
histogram_quantile(0.99, sum(rate(http_server_requests_seconds_bucket{job="employeeapp"}[5m])) by (le))

# Error rate (4xx + 5xx)
sum(rate(http_server_requests_seconds_count{job="employeeapp",status=~"[45].."}[1m])) / sum(rate(http_server_requests_seconds_count{job="employeeapp"}[1m]))

# 5xx error rate
sum(rate(http_server_requests_seconds_count{job="employeeapp",status=~"5.."}[1m]))
```

### 10.3 JVM Metrics

```promql
# Heap memory usage
jvm_memory_used_bytes{job="employeeapp",area="heap"}

# Heap memory utilization ratio
jvm_memory_used_bytes{job="employeeapp",area="heap"} / jvm_memory_max_bytes{job="employeeapp",area="heap"}

# CPU usage
rate(process_cpu_seconds_total{job="employeeapp"}[1m])

# Thread count
jvm_threads_live_threads{job="employeeapp"}

# GC pause time (avg)
rate(jvm_gc_pause_seconds_sum{job="employeeapp"}[5m]) / rate(jvm_gc_pause_seconds_count{job="employeeapp"}[5m])
```

### 10.4 Database Metrics

```promql
# Active connections
hikaricp_connections_active{job="employeeapp"}

# Idle connections
hikaricp_connections_idle{job="employeeapp"}

# Pending threads waiting for connection
hikaricp_connections_pending{job="employeeapp"}

# Connection pool usage ratio
hikaricp_connections_active{job="employeeapp"} / hikaricp_connections_max{job="employeeapp"}
```

### 10.5 Kubernetes Container Metrics

```promql
# Container memory usage
container_memory_working_set_bytes{namespace="employeeapp",container="employeeapp"}

# Container CPU usage (cores)
sum(rate(container_cpu_usage_seconds_total{namespace="employeeapp",container="employeeapp"}[1m])) by (pod)

# Container restart count
kube_pod_container_status_restarts_total{namespace="employeeapp",container="employeeapp"}
```

---

## 11. Alerting

### 11.1 Recommended Alert Rules

Create a `prometheus-rules.yaml` ConfigMap for alerting:

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-rules
  namespace: employeeapp
data:
  rules.yml: |
    groups:
      - name: employeeapp-alerts
        rules:
          - alert: HighErrorRate
            expr: sum(rate(http_server_requests_seconds_count{job="employeeapp",status=~"5.."}[1m])) / sum(rate(http_server_requests_seconds_count{job="employeeapp"}[1m])) > 0.05
            for: 5m
            labels:
              severity: critical
            annotations:
              summary: "High 5xx error rate (>5%)"
              description: "Employeeapp has >5% 5xx errors for 5 minutes"

          - alert: HighResponseTime
            expr: histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{job="employeeapp"}[5m])) by (le)) > 1
            for: 5m
            labels:
              severity: warning
            annotations:
              summary: "High p95 response time (>1s)"
              description: "Employeeapp p95 response time exceeds 1 second"

          - alert: HighHeapUsage
            expr: jvm_memory_used_bytes{job="employeeapp",area="heap"} / jvm_memory_max_bytes{job="employeeapp",area="heap"} > 0.85
            for: 5m
            labels:
              severity: warning
            annotations:
              summary: "High JVM heap usage (>85%)"
              description: "Employeeapp heap usage exceeds 85%"

          - alert: PodDown
            expr: up{job="employeeapp"} == 0
            for: 1m
            labels:
              severity: critical
            annotations:
              summary: "Employeeapp pod is down"
              description: "Pod {{ $labels.pod }} has been down for 1 minute"

          - alert: HighGCPressure
            expr: rate(jvm_gc_pause_seconds_sum{job="employeeapp"}[5m]) / rate(jvm_gc_pause_seconds_count{job="employeeapp"}[5m]) > 0.5
            for: 5m
            labels:
              severity: warning
            annotations:
              summary: "High GC pause time (>500ms avg)"
              description: "Employeeapp GC pauses exceed 500ms average"

          - alert: ConnectionPoolExhaustion
            expr: hikaricp_connections_pending{job="employeeapp"} > 0
            for: 2m
            labels:
              severity: warning
            annotations:
              summary: "Database connection pool exhausted"
              description: "Threads waiting for DB connections"
```

### 11.2 Enable Alert Rules

Add to `prometheus-configmap.yaml`:

```yaml
rule_files:
  - '/etc/prometheus/rules/*.yml'
```

And mount the rules ConfigMap in `prometheus-deployment.yaml`:

```yaml
volumeMounts:
  - name: prometheus-rules
    mountPath: /etc/prometheus/rules
volumes:
  - name: prometheus-rules
    configMap:
      name: prometheus-rules
```

---

## 12. Accessing via Ingress

### 12.1 Add Ingress Rules

Add the following to your `ingress.yaml`:

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: employeeapp-ingress
  namespace: employeeapp
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - employeeapp.example.com
        - prometheus.example.com
        - grafana.example.com
      secretName: employeeapp-tls-secret
  rules:
    - host: employeeapp.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: employeeapp
                port:
                  number: 80
    - host: prometheus.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: prometheus
                port:
                  number: 9090
    - host: grafana.example.com
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: grafana
                port:
                  number: 3000
```

### 12.2 Update DNS

Add entries to your `/etc/hosts` (local) or DNS:

```
<ingress-ip>  employeeapp.example.com prometheus.example.com grafana.example.com
```

---

## 13. Troubleshooting

### 13.1 Prometheus Not Scraping Employeeapp

```powershell
# Check Prometheus targets
kubectl port-forward -n employeeapp svc/prometheus 9090:9090
# Visit: http://localhost:9090/targets

# Check if employeeapp pods have the correct labels
kubectl get pods -n employeeapp --show-labels | Select-String "employeeapp"

# Verify metrics endpoint is accessible
kubectl exec -n employeeapp deployment/employeeapp -- curl -s http://localhost:8080/actuator/prometheus | Select-Object -First 5

# Check Prometheus logs
kubectl logs -n employeeapp deployment/prometheus
```

**Common causes:**
- Missing `app.kubernetes.io/name: employeeapp` label on pods
- Actuator prometheus endpoint not exposed (check `management.endpoints.web.exposure.include`)
- Missing `micrometer-registry-prometheus` dependency

### 13.2 Grafana Dashboard Not Loading

```powershell
# Check Grafana logs
kubectl logs -n employeeapp deployment/grafana

# Verify datasource is configured
kubectl port-forward -n employeeapp svc/grafana 3000:3000
# Visit: http://localhost:3000/connections/datasources

# Check dashboard files are mounted
kubectl exec -n employeeapp deployment/grafana -- ls -la /var/lib/grafana/dashboards/

# Verify provisioning config
kubectl exec -n employeeapp deployment/grafana -- cat /etc/grafana/provisioning/datasources/*.yaml
```

### 13.3 No Data in Dashboards

```powersql
# Check if Prometheus has data
kubectl port-forward -n employeeapp svc/prometheus 9090:9090
# Query in Prometheus UI: up{job="employeeapp"}

# Check scrape interval - wait at least 10-15 seconds after deployment
# Verify the job name matches: should be "employeeapp"

# Check pod labels match the relabel config
kubectl get pods -n employeeapp -o jsonpath='{.items[*].metadata.labels}'
```

### 13.4 Grafana PVC Not Bound

```powershell
# Check PVC status
kubectl get pvc -n employeeapp | Select-String "grafana"

# Check available storage classes
kubectl get storageclass

# If 'standard' doesn't exist, use the available class name
# Update grafana-pvc.yaml with the correct storageClassName
```

### 13.5 High Memory Usage

```powershell
# Check resource usage
kubectl top pods -n employeeapp

# If Prometheus uses too much memory, reduce retention
# Edit prometheus-deployment.yaml:
#   --storage.tsdb.retention.time=7d
#   --storage.tsdb.retention.size=2GB
```

---

## 14. Resource Requirements

### 14.1 Total Resource Requests

| Component | CPU Request | CPU Limit | Memory Request | Memory Limit |
|---|---|---|---|---|
| **Prometheus** | 200m | 500m | 512Mi | 1Gi |
| **Grafana** | 100m | 300m | 256Mi | 512Mi |
| **Employeeapp (×3)** | 250m × 3 | 500m × 3 | 512Mi × 3 | 1Gi × 3 |
| **MySQL** | 250m | 500m | 512Mi | 1Gi |
| **Total** | ~1.2 CPU | ~2.8 CPU | ~2.5 Gi | ~5.5 Gi |

### 14.2 Storage

| Component | PVC | Size | Purpose |
|---|---|---|---|
| **MySQL** | `mysql-pvc` | 5Gi | Database data |
| **Grafana** | `grafana-pvc` | 2Gi | Dashboards, users |
| **Prometheus** | `emptyDir` | Ephemeral | Time-series data |

> **Note:** Prometheus uses `emptyDir` by default. For production, replace with a PVC for persistent metrics storage.

---

## 15. Quick Reference

### 15.1 Most Common Commands

```powershell
# Deploy monitoring stack
kubectl apply -f kubernetes/prometheus-*.yaml -f kubernetes/grafana-*.yaml

# Access Prometheus
kubectl port-forward -n employeeapp svc/prometheus 9090:9090

# Access Grafana
kubectl port-forward -n employeeapp svc/grafana 3000:3000

# Check monitoring pods
kubectl get pods -n employeeapp | Select-String "prometheus|grafana"

# View Prometheus targets
kubectl port-forward -n employeeapp svc/prometheus 9090:9090
# http://localhost:9090/targets

# View employeeapp metrics directly
kubectl exec -n employeeapp deployment/employeeapp -- curl -s http://localhost:8080/actuator/prometheus

# Reload Prometheus config
kubectl exec -n employeeapp deployment/prometheus -- curl -X POST http://localhost:9090/-/reload

# Delete monitoring stack
kubectl delete -f kubernetes/prometheus-*.yaml -f kubernetes/grafana-*.yaml
```

### 15.2 URLs

| Service | Port-Forward URL | Ingress URL |
|---|---|---|
| **Prometheus** | `http://localhost:9090` | `http://prometheus.example.com` |
| **Grafana** | `http://localhost:3000` | `http://grafana.example.com` |
| **Employeeapp** | `http://localhost:8080` | `http://employeeapp.example.com` |

### 15.3 Grafana Credentials

| Field | Value |
|---|---|
| **Username** | `admin` |
| **Password** | `admin` |

---

## Appendix: Adding Custom Metrics

To add custom business metrics to your Spring Boot application:

```java
@Configuration
public class MetricsConfig {
    private final MeterRegistry registry;

    public MetricsConfig(MeterRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void init() {
        // Counter: total employees created
        registry.counter("employees.created.total");

        // Gauge: current employee count
        registry.gauge("employees.current.count", 0.0);

        // Timer: employee search duration
        registry.timer("employees.search.duration");
    }
}
```

These custom metrics will automatically appear in Prometheus and can be added to Grafana dashboards.
