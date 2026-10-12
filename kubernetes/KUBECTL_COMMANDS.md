# Kubectl Commands Reference — Employee Management Application

Complete reference of `kubectl` commands for deploying, managing, scaling, and troubleshooting the Employee Management Application on Kubernetes.

---

## Table of Contents

1. [Kubernetes Resource Kinds Reference](#1-kubernetes-resource-kinds-reference)
2. [Prerequisites & Cluster Setup](#2-prerequisites--cluster-setup)
3. [Namespace Management](#3-namespace-management)
4. [Full Deployment](#4-full-deployment)
5. [Deployment Verification](#5-deployment-verification)
6. [Scaling](#6-scaling)
7. [Updates & Rollouts](#7-updates--rollouts)
8. [Monitoring & Logging](#8-monitoring--logging)
9. [Configuration Management](#9-configuration-management)
10. [Accessing the Application](#10-accessing-the-application)
11. [Database Management](#11-database-management)
12. [Ingress & Networking](#12-ingress--networking)
13. [Troubleshooting](#13-troubleshooting)
14. [Maintenance](#14-maintenance)
15. [Cleanup](#15-cleanup)
16. [Minikube Local Development](#16-minikube-local-development)
17. [Production Commands](#17-production-commands)

---

## 1. Kubernetes Resource Kinds Reference

This section documents every Kubernetes resource **Kind** used in the `kubernetes/` directory. Understanding resource kinds is essential for writing, reading, and troubleshooting Kubernetes manifests.

### 1.1 Resource Kinds Overview

Each Kubernetes manifest file defines a resource with a specific `kind`. The `kind` tells Kubernetes what type of object to create.

| Kind | File(s) | apiVersion | kubectl Short Name | Purpose |
|---|---|---|---|---|
| `Namespace` | `namespace.yaml` | `v1` | `ns` | Logical isolation for all app resources |
| `Secret` | `secret.yaml` | `v1` | `secret` | Sensitive data (DB credentials) |
| `ConfigMap` | `configmap.yaml` | `v1` | `configmap` / `cm` | Non-sensitive configuration (env vars) |
| `PersistentVolumeClaim` | `mysql-pvc.yaml` | `v1` | `pvc` | Persistent storage for MySQL data |
| `Deployment` | `deployment.yaml`, `mysql-deployment.yaml` | `apps/v1` | `deploy` | Manages replicated pods with self-healing |
| `Service` | `service.yaml`, `mysql-service.yaml` | `v1` | `svc` | Stable network endpoint for pods |
| `HorizontalPodAutoscaler` | `hpa.yaml` | `autoscaling/v2` | `hpa` | Auto-scales pods based on CPU/memory |
| `Ingress` | `ingress.yaml` | `networking.k8s.io/v1` | `ing` / `ingress` | External HTTP/S access and routing |

### 1.2 Resource Dependency Graph

Resources must be created in the correct order. Arrows show dependencies (A → B means A must exist before B):

```
Namespace
    ↓
Secret ────────┐
ConfigMap ─────┼──→ Deployment (employeeapp)
               │         ↓
PVC ───────────┼──→ Deployment (mysql)
               │         ↑
Service (mysql)├──────────┘
               │
               └──→ Service (employeeapp) ←→ HPA
                                      ↓
                                   Ingress
```

**Deployment order:**
1. `Namespace` — Create the isolated space first
2. `Secret` — Database credentials needed by both deployments
3. `ConfigMap` — Application configuration for the app deployment
4. `PersistentVolumeClaim` — Storage for MySQL before it starts
5. `Deployment (mysql)` + `Service (mysql)` — Database must be ready before the app
6. `Deployment (employeeapp)` + `Service (employeeapp)` — App connects to MySQL
7. `HorizontalPodAutoscaler` — Monitors the app deployment
8. `Ingress` — Routes external traffic to the app service

### 1.3 Detailed Kind Reference

#### Namespace (`kind: Namespace`)

**What it does:** Creates a logical boundary (sandbox) for all application resources. Namespaces isolate resources, allow resource quotas, and simplify management.

**Key fields:**
- `metadata.name` — Unique name across the cluster (e.g., `employeeapp`)
- `metadata.labels` — Labels for organization and selection

**File:** `namespace.yaml`

**kubectl commands:**
```powershell
kubectl get ns
kubectl get namespace employeeapp -o yaml
kubectl describe namespace employeeapp
```

---

#### Secret (`kind: Secret`)

**What it does:** Stores sensitive data (passwords, tokens, keys) as base64-encoded values. Secrets are mounted into pods as environment variables or volumes.

**Key fields:**
- `type: Opaque` — Generic secret for arbitrary user-defined data
- `stringData` — Plain-text key-value pairs (automatically base64-encoded by Kubernetes)
- `data` — Pre-base64-encoded key-value pairs (use `stringData` instead for clarity)

**File:** `secret.yaml`

**Data stored:**
| Key | Value | Used By |
|---|---|---|
| `DB_USERNAME` | `root` | Employeeapp deployment, MySQL deployment |
| `DB_PASSWORD` | `rootpassword123` | Employeeapp deployment, MySQL deployment |

**kubectl commands:**
```powershell
kubectl get secret -n employeeapp
kubectl describe secret employeeapp-secret -n employeeapp
# Decode a secret value
kubectl get secret employeeapp-secret -n employeeapp -o jsonpath='{.data.DB_PASSWORD}' | base64 --decode
```

**Security note:** Never commit real secrets to version control. In production, use Sealed Secrets, External Secrets Operator, or a vault solution.

---

#### ConfigMap (`kind: ConfigMap`)

**What it does:** Stores non-sensitive configuration data as key-value pairs. ConfigMaps decouple configuration from container images, making apps portable across environments.

**Key fields:**
- `data` — Key-value pairs of configuration strings

**File:** `configmap.yaml`

**Configuration stored:**
| Key | Value | Purpose |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` | Activates MySQL profile in Spring Boot |
| `DB_HOST` | `mysql` | MySQL service DNS name (cluster-internal) |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `employeedb` | Database name |
| `JAVA_OPTS` | JVM flags | Container-aware memory and GC settings |

**kubectl commands:**
```powershell
kubectl get configmap -n employeeapp
kubectl get cm employeeapp-config -n employeeapp -o yaml
kubectl describe configmap employeeapp-config -n employeeapp
```

---

#### PersistentVolumeClaim (`kind: PersistentVolumeClaim`)

**What it does:** Requests persistent storage from the cluster. Unlike pods, which are ephemeral, PVCs survive pod restarts — essential for databases that must retain data.

**Key fields:**
- `accessModes: ReadWriteOnce` — Mounted as read-write by a single node
- `resources.requests.storage: 5Gi` — Requests 5 GB of storage
- `storageClassName: standard` — Specifies which storage provisioner to use

**File:** `mysql-pvc.yaml`

**kubectl commands:**
```powershell
kubectl get pvc -n employeeapp
kubectl describe pvc mysql-pvc -n employeeapp
# Check backing PersistentVolume
kubectl get pv -n employeeapp
```

**Access Modes reference:**
| Mode | Description |
|---|---|
| `ReadWriteOnce` (RWO) | One node, read-write |
| `ReadOnlyMany` (ROX) | Many nodes, read-only |
| `ReadWriteMany` (RWX) | Many nodes, read-write |
| `ReadWriteOncePod` (RWOP) | Single Pod, read-write |

---

#### Deployment (`kind: Deployment`)

**What it does:** The most commonly used Kubernetes resource. A Deployment manages a set of identical pods, providing:
- **Self-healing:** Automatically restarts failed pods
- **Scaling:** Change replica count up or down
- **Rolling updates:** Zero-downtime deployments
- **Rollbacks:** Revert to previous versions

**Key fields:**
- `spec.replicas` — Desired number of pod copies
- `spec.selector.matchLabels` — Labels to identify which pods this deployment manages
- `spec.strategy` — Update strategy (RollingUpdate, Recreate)
- `spec.template` — Pod template defining containers, env, resources, probes

**Files:** `deployment.yaml` (employeeapp, 3 replicas), `mysql-deployment.yaml` (mysql, 1 replica)

**Employeeapp Deployment highlights:**
| Feature | Configuration |
|---|---|
| Replicas | 3 |
| Image | `employeeapp:latest` |
| Container port | 8080 |
| CPU request/limit | 250m / 500m |
| Memory request/limit | 512Mi / 1Gi |
| Liveness probe | `/actuator/health/liveness` every 10s |
| Readiness probe | `/actuator/health/readiness` every 5s |
| Startup probe | `/actuator/health` every 5s, 10 failures |
| Rolling update | maxSurge=1, maxUnavailable=0 |

**kubectl commands:**
```powershell
kubectl get deploy -n employeeapp
kubectl get deployment employeeapp -n employeeapp -o wide
kubectl describe deployment employeeapp -n employeeapp
kubectl scale deployment/employeeapp --replicas=5 -n employeeapp
kubectl rollout status deployment/employeeapp -n employeeapp
kubectl rollout history deployment/employeeapp -n employeeapp
kubectl rollout undo deployment/employeeapp -n employeeapp
```

---

#### Service (`kind: Service`)

**What it does:** Provides a stable network endpoint (DNS name + IP) for a set of pods. Since pods are ephemeral and get new IPs on restart, Services provide stable access.

**Key fields:**
- `spec.type` — Service type (ClusterIP, NodePort, LoadBalancer)
- `spec.ports` — Port mapping (service port → container port)
- `spec.selector` — Labels to select which pods receive traffic

**Files:** `service.yaml` (employeeapp), `mysql-service.yaml` (mysql)

**Service types:**
| Type | Description | Use Case |
|---|---|---|
| `ClusterIP` (default) | Internal-only, cluster-internal IP | Service-to-service communication |
| `NodePort` | Exposes on each node's IP at a static port | Direct node access |
| `LoadBalancer` | Provisions external cloud load balancer | Cloud production |
| `ExternalName` | Maps to a DNS name | External services |

**Employeeapp Service:**
- Type: `ClusterIP` (internal only; Ingress handles external access)
- Port: 80 → 8080
- Selector: `app.kubernetes.io/name=employeeapp`
- DNS: `employeeapp.employeeapp.svc.cluster.local`

**MySQL Service:**
- Type: `ClusterIP` (internal only)
- Port: 3306 → 3306
- Selector: `app.kubernetes.io/name=mysql`
- DNS: `mysql.employeeapp.svc.cluster.local`

**kubectl commands:**
```powershell
kubectl get svc -n employeeapp
kubectl describe svc employeeapp -n employeeapp
# Test DNS resolution from a pod
kubectl run dns-test --image=busybox --restart=Never -n employeeapp --rm -it -- nslookup mysql
```

---

#### HorizontalPodAutoscaler (`kind: HorizontalPodAutoscaler`)

**What it does:** Automatically scales the number of pod replicas up or down based on observed metrics (CPU, memory, or custom metrics). Eliminates manual scaling.

**Key fields:**
- `scaleTargetRef` — References the Deployment to scale
- `minReplicas` / `maxReplicas` — Replica bounds
- `metrics` — Conditions that trigger scaling
- `behavior` — Controls scaling speed (stabilization windows, policies)

**File:** `hpa.yaml`

**Current configuration:**
| Setting | Value |
|---|---|
| Target | Deployment `employeeapp` |
| Min replicas | 2 |
| Max replicas | 10 |
| CPU target | 70% average utilization |
| Memory target | 80% average utilization |
| Scale up | Max 2 pods per 60s, 60s stabilization |
| Scale down | Max 1 pod per 120s, 300s stabilization |

**kubectl commands:**
```powershell
kubectl get hpa -n employeeapp
kubectl describe hpa employeeapp-hpa -n employeeapp
kubectl get hpa -n employeeapp -w  # Watch auto-scaling in real-time
kubectl top pods -n employeeapp     # Current resource usage
```

---

#### Ingress (`kind: Ingress`)

**What it does:** Manages external HTTP/HTTPS access to services. An Ingress acts as a smart reverse proxy, routing traffic based on hostnames and paths. Requires an Ingress Controller (e.g., NGINX) to function.

**Key fields:**
- `ingressClassName` — Which ingress controller handles this resource (e.g., `nginx`)
- `tls` — TLS/SSL certificate configuration
- `rules` — Host-based and path-based routing rules
- `annotations` — Controller-specific configuration (NGINX settings)

**File:** `ingress.yaml`

**Current configuration:**
| Setting | Value |
|---|---|
| Ingress class | `nginx` |
| Host | `employeeapp.example.com` |
| Path | `/` (Prefix — matches all paths) |
| Backend | Service `employeeapp`, port 80 |
| TLS | Enabled (secret: `employeeapp-tls-secret`) |
| SSL redirect | `true` |
| Proxy body size | 10m |
| Proxy timeout | 60s |

**kubectl commands:**
```powershell
kubectl get ingress -n employeeapp
kubectl get ing -n employeeapp -o wide
kubectl describe ingress employeeapp-ingress -n employeeapp
```

### 1.4 Kind-Specific kubectl Short Names

| Kind | Short name | Plural | Example |
|---|---|---|---|
| Namespace | `ns` | `namespaces` | `kubectl get ns` |
| Secret | `secret` | `secrets` | `kubectl get secret -n employeeapp` |
| ConfigMap | `cm` | `configmaps` | `kubectl get cm -n employeeapp` |
| PersistentVolumeClaim | `pvc` | `persistentvolumeclaims` | `kubectl get pvc -n employeeapp` |
| Deployment | `deploy` | `deployments` | `kubectl get deploy -n employeeapp` |
| Service | `svc` | `services` | `kubectl get svc -n employeeapp` |
| HorizontalPodAutoscaler | `hpa` | `horizontalpodautoscalers` | `kubectl get hpa -n employeeapp` |
| Ingress | `ing` | `ingresses` | `kubectl get ing -n employeeapp` |
| Pod | `po` | `pods` | `kubectl get pods -n employeeapp` |
| PersistentVolume | `pv` | `persistentvolumes` | `kubectl get pv` |
| Node | `no` | `nodes` | `kubectl get nodes` |
| Event | `ev` | `events` | `kubectl get events -n employeeapp` |

### 1.5 Listing Resources by Kind

```powershell
# List all resources of a specific kind
kubectl get <kind> -n employeeapp

# Examples
kubectl get pods -n employeeapp
kubectl get deployments -n employeeapp
kubectl get services -n employeeapp
kubectl get configmaps -n employeeapp
kubectl get secrets -n employeeapp
kubectl get pvc -n employeeapp
kubectl get hpa -n employeeapp
kubectl get ingress -n employeeapp

# List ALL resources in the namespace
kubectl get all -n employeeapp

# List ALL resources including non-standard kinds
kubectl api-resources --namespaced=true -o name | ForEach-Object { kubectl get $_ -n employeeapp 2>$null }

# Get resources with their Kind column
kubectl get all -n employeeapp -o custom-columns=KIND:.kind,NAME:.metadata.name,STATUS:.status.phase
```

---

## 2. Prerequisites & Cluster Setup

### Check kubectl installation

```powershell
kubectl version --client --short
kubectl cluster-info
```

### Verify cluster connectivity

```powershell
# Check if you can communicate with the cluster
kubectl cluster-info

# Check node status
kubectl get nodes

# Check node details
kubectl describe node <node-name>

# Check cluster components
kubectl get componentstatuses
```

### Check required addons (Minikube)

```powershell
# Enable Ingress Controller
minikube addons enable ingress

# Enable Metrics Server (required for HPA)
minikube addons enable metrics-server

# Enable storage provisioner
minikube addons enable storage-provisioner

# List all addons
minikube addons list
```

### Verify Metrics Server is running (for HPA)

```powershell
kubectl get deployment metrics-server -n kube-system
kubectl top nodes
kubectl top pods -n employeeapp
```

---

## 2. Namespace Management

### Create namespace

```powershell
kubectl apply -f kubernetes/namespace.yaml
```

### Verify namespace

```powershell
kubectl get namespace employeeapp
kubectl get namespace employeeapp -o yaml
```

### Set default namespace (avoid typing `-n employeeapp` repeatedly)

```powershell
# Option 1: Context-based default
kubectl config set-context --current --namespace=employeeapp

# Option 2: Per-command flag
kubectl get pods -n employeeapp

# Option 3: Environment variable
$env:KUBE_NAMESPACE = "employeeapp"
```

### View namespace resources

```powershell
kubectl get all -n employeeapp
kubectl get all -n employeeapp -o wide
kubectl get all -n employeeapp -o yaml
```

---

## 3. Full Deployment

### Deploy step by step (recommended order)

```powershell
# 1. Create namespace
kubectl apply -f kubernetes/namespace.yaml

# 2. Create secrets (database credentials)
kubectl apply -f kubernetes/secret.yaml

# 3. Create configmap (application configuration)
kubectl apply -f kubernetes/configmap.yaml

# 4. Create persistent volume claim (MySQL storage)
kubectl apply -f kubernetes/mysql-pvc.yaml

# 5. Deploy MySQL database
kubectl apply -f kubernetes/mysql-deployment.yaml
kubectl apply -f kubernetes/mysql-service.yaml

# 6. Wait for MySQL to be ready
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=mysql -n employeeapp --timeout=120s

# 7. Deploy the application
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml

# 8. Wait for application to be ready
kubectl wait --for=condition=available deployment/employeeapp -n employeeapp --timeout=120s

# 9. Apply Ingress (optional, requires NGINX Ingress Controller)
kubectl apply -f kubernetes/ingress.yaml

# 10. Apply HPA (optional, requires Metrics Server)
kubectl apply -f kubernetes/hpa.yaml
```

### Deploy all at once

```powershell
# Apply all manifests in the directory
kubectl apply -f kubernetes/

# Apply with dry-run to preview changes
kubectl apply -f kubernetes/ --dry-run=client

# Apply and see what would change
kubectl apply -f kubernetes/ --dry-run=server
```

### Build and push Docker image

```powershell
# Build the image
docker build -t employeeapp:latest .

# Tag for a registry
docker tag employeeapp:latest myregistry.com/employeeapp:latest

# Push to registry
docker push myregistry.com/employeeapp:latest

# For Minikube, load directly
minikube image build -t employeeapp:latest .
```

---

## 4. Deployment Verification

### Check all resources

```powershell
# Pods
kubectl get pods -n employeeapp
kubectl get pods -n employeeapp -o wide

# Deployments
kubectl get deployments -n employeeapp
kubectl get deployments -n employeeapp -o wide

# Services
kubectl get svc -n employeeapp
kubectl get svc -n employeeapp -o wide

# ConfigMaps
kubectl get configmap -n employeeapp

# Secrets
kubectl get secret -n employeeapp

# PersistentVolumeClaims
kubectl get pvc -n employeeapp

# HPA
kubectl get hpa -n employeeapp

# Ingress
kubectl get ingress -n employeeapp

# Events (sorted by time)
kubectl get events -n employeeapp --sort-by='.lastTimestamp'
```

### Detailed resource inspection

```powershell
# Describe a pod
kubectl describe pod <pod-name> -n employeeapp

# Describe a deployment
kubectl describe deployment employeeapp -n employeeapp

# Describe a service
kubectl describe svc employeeapp -n employeeapp

# Describe HPA
kubectl describe hpa employeeapp-hpa -n employeeapp

# Describe PVC
kubectl describe pvc mysql-pvc -n employeeapp
```

### Check readiness and liveness

```powershell
# Check pod conditions
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.status.conditions[*].type}={.status.conditions[*].status}{"\n"}{end}'

# Check readiness probe
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/actuator/health/readiness

# Check liveness probe
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/actuator/health/liveness

# Check overall health
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/actuator/health
```

### Verify application endpoints

```powershell
# Test API endpoint
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/api/employees

# Test Swagger UI
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/swagger-ui.html

# Test API docs
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/api-docs
```

### JSON/YAML output for scripting

```powershell
# Get pod names
kubectl get pods -n employeeapp -o jsonpath='{.items[*].metadata.name}'

# Get pod IPs
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.status.podIP}{"\n"}{end}'

# Get service cluster IP
kubectl get svc employeeapp -n employeeapp -o jsonpath='{.spec.clusterIP}'

# Get replica count
kubectl get deployment employeeapp -n employeeapp -o jsonpath='{.spec.replicas}'

# Get current ready replicas
kubectl get deployment employeeapp -n employeeapp -o jsonpath='{.status.readyReplicas}'
```

---

## 5. Scaling

### Manual scaling

```powershell
# Scale to specific replica count
kubectl scale deployment/employeeapp --replicas=5 -n employeeapp

# Scale up by 2
kubectl scale deployment/employeeapp --replicas=+2 -n employeeapp

# Scale down by 1
kubectl scale deployment/employeeapp --replicas=-1 -n employeeapp

# Verify scaling
kubectl get deployment employeeapp -n employeeapp -w

# Check pod distribution across nodes
kubectl get pods -n employeeapp -o wide
```

### Horizontal Pod Autoscaler (HPA)

```powershell
# View HPA status
kubectl get hpa -n employeeapp
kubectl get hpa employeeapp-hpa -n employeeapp -o wide

# Describe HPA for detailed metrics
kubectl describe hpa employeeapp-hpa -n employeeapp

# Watch HPA scaling in real-time
kubectl get hpa -n employeeapp -w

# Edit HPA configuration
kubectl edit hpa employeeapp-hpa -n employeeapp

# Apply updated HPA manifest
kubectl apply -f kubernetes/hpa.yaml

# Check current metrics
kubectl top pods -n employeeapp
```

### HPA configuration reference

Current HPA settings in `hpa.yaml`:

| Setting | Value | Description |
|---|---|---|
| minReplicas | 2 | Minimum number of pods |
| maxReplicas | 10 | Maximum number of pods |
| CPU target | 70% | Scale when average CPU exceeds 70% |
| Memory target | 80% | Scale when average memory exceeds 80% |
| Scale up policy | 2 pods/min | Maximum 2 new pods per 60 seconds |
| Scale down policy | 1 pod/2min | Maximum 1 pod removed per 120 seconds |
| Scale down stabilization | 300s | Wait 5 minutes before scaling down |

### Custom HPA commands

```powershell
# Create HPA with custom targets
kubectl autoscale deployment/employeeapp --min=3 --max=20 --cpu-percent=60 -n employeeapp

# Create HPA with memory target
kubectl autoscale deployment/employeeapp --min=2 --max=10 --cpu-percent=70 --memory-percent=80 -n employeeapp

# Remove HPA (revert to manual scaling)
kubectl delete hpa employeeapp-hpa -n employeeapp
```

### Simulate load testing (to trigger auto-scaling)

```powershell
# Create a load-test pod
kubectl run load-test --image=busybox --restart=Never -n employeeapp -- /bin/sh -c "while true; do wget -qO- http://employeeapp:80/api/employees; done"

# Monitor scaling
watch kubectl get hpa -n employeeapp

# Clean up load test
kubectl delete pod load-test -n employeeapp
```

---

## 6. Updates & Rollouts

### Deploy a new version

```powershell
# Build new image
docker build -t employeeapp:v1.1 .

# Update the deployment image
kubectl set image deployment/employeeapp employeeapp=employeeapp:v1.1 -n employeeapp

# Monitor rollout progress
kubectl rollout status deployment/employeeapp -n employeeapp

# Watch pods during rollout
kubectl get pods -n employeeapp -w
```

### Rollout management

```powershell
# Check rollout status
kubectl rollout status deployment/employeeapp -n employeeapp

# View rollout history
kubectl rollout history deployment/employeeapp -n employeeapp

# View history with revision details
kubectl rollout history deployment/employeeapp --revision=2 -n employeeapp

# Pause rollout (for manual verification)
kubectl rollout pause deployment/employeeapp -n employeeapp

# Resume paused rollout
kubectl rollout resume deployment/employeeapp -n employeeapp
```

### Rollback

```powershell
# Rollback to previous version
kubectl rollout undo deployment/employeeapp -n employeeapp

# Rollback to specific revision
kubectl rollout undo deployment/employeeapp --to-revision=2 -n employeeapp

# Monitor rollback
kubectl rollout status deployment/employeeapp -n employeeapp
```

### Rolling update strategy

Current strategy in `deployment.yaml`:

| Setting | Value | Description |
|---|---|---|
| type | RollingUpdate | Zero-downtime deployment |
| maxSurge | 1 | Create 1 extra pod during update |
| maxUnavailable | 0 | Never take pods down during update |

```powershell
# Update rolling update strategy
kubectl patch deployment employeeapp -n employeeapp -p '{"spec":{"strategy":{"rollingUpdate":{"maxSurge":"25%","maxUnavailable":"0%"}}}}'

# Verify strategy
kubectl get deployment employeeapp -n employeeapp -o jsonpath='{.spec.strategy}'
```

### Canary-style deployment (manual)

```powershell
# Create a canary deployment with 1 replica
kubectl set image deployment/employeeapp employeeapp=employeeapp:v2.0-canary -n employeeapp
kubectl scale deployment/employeeapp --replicas=1 -n employeeapp

# Test the canary
kubectl exec -it <canary-pod> -n employeeapp -- wget -qO- http://localhost:8080/api/employees

# If successful, scale up
kubectl scale deployment/employeeapp --replicas=3 -n employeeapp

# If not, rollback
kubectl rollout undo deployment/employeeapp -n employeeapp
```

---

## 7. Monitoring & Logging

### Application logs

```powershell
# Follow logs of all app pods
kubectl logs -l app.kubernetes.io/name=employeeapp -n employeeapp -f

# Follow logs of a specific pod
kubectl logs <pod-name> -n employeeapp -f

# Get last 100 lines
kubectl logs <pod-name> -n employeeapp --tail=100

# Get logs with timestamps
kubectl logs <pod-name> -n employeeapp -f --timestamps

# Get logs from the last hour
kubectl logs <pod-name> -n employeeapp --since=1h

# Get logs from previous container (if restarted)
kubectl logs <pod-name> -n employeeapp -f --previous

# Filter logs by keyword
kubectl logs <pod-name> -n employeeapp -f | Select-String "ERROR"
```

### MySQL logs

```powershell
# Follow MySQL logs
kubectl logs -l app.kubernetes.io/name=mysql -n employeeapp -f

# Get specific MySQL pod logs
kubectl logs <mysql-pod-name> -n employeeapp --tail=50
```

### Resource usage

```powershell
# Node resource usage
kubectl top nodes

# Pod resource usage
kubectl top pods -n employeeapp

# Pod resource usage with labels
kubectl top pods -n employeeapp --sort-by=cpu

# Deployment resource summary
kubectl top pods -n employeeapp -l app.kubernetes.io/name=employeeapp
```

### Pod health checks

```powershell
# Check all pod statuses
kubectl get pods -n employeeapp

# Check pod restart count
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.status.containerStatuses[*].restartCount}{"\n"}{end}'

# Check container states
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.status.containerStatuses[*].state}{"\n"}{end}'

# Describe pod for health probe status
kubectl describe pod <pod-name> -n employeeapp | Select-String "Liveness|Readiness|Startup"
```

### Events monitoring

```powershell
# Recent events
kubectl get events -n employeeapp --sort-by='.lastTimestamp'

# Watch events in real-time
kubectl get events -n employeeapp --watch

# Filter events by type
kubectl get events -n employeeapp --field-selector type=Warning

# All cluster events
kubectl get events --all-namespaces --sort-by='.lastTimestamp'
```

---

## 8. Configuration Management

### ConfigMap operations

```powershell
# View ConfigMap
kubectl get configmap employeeapp-config -n employeeapp -o yaml

# View specific key
kubectl get configmap employeeapp-config -n employeeapp -o jsonpath='{.data.DB_HOST}'

# Edit ConfigMap
kubectl edit configmap employeeapp-config -n employeeapp

# Update a single value
kubectl patch configmap employeeapp-config -n employeeapp -p '{"data":{"DB_HOST":"mysql-new"}}'

# Create ConfigMap from literal values
kubectl create configmap employeeapp-config --from-literal=KEY=value -n employeeapp --dry-run=client -o yaml

# Create ConfigMap from file
kubectl create configmap employeeapp-config --from-file=application.properties -n employeeapp --dry-run=client -o yaml
```

### Secret operations

```powershell
# View Secret (decoded)
kubectl get secret employeeapp-secret -n employeeapp -o jsonpath='{.data.DB_PASSWORD}' | ForEach-Object { [System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($_)) }

# View Secret (all keys)
kubectl get secret employeeapp-secret -n employeeapp -o yaml

# Edit Secret
kubectl edit secret employeeapp-secret -n employeeapp

# Update password
kubectl patch secret employeeapp-secret -n employeeapp -p "{\"stringData\":{\"DB_PASSWORD\":\"new-password\"}}"

# Create secret from literals
kubectl create secret generic employeeapp-secret --from-literal=DB_USERNAME=root --from-literal=DB_PASSWORD=password -n employeeapp --dry-run=client -o yaml
```

### Restart after config change

```powershell
# Rolling restart (recommended)
kubectl rollout restart deployment/employeeapp -n employeeapp

# Monitor restart
kubectl rollout status deployment/employeeapp -n employeeapp

# Alternative: delete pods (deployment recreates them)
kubectl delete pods -l app.kubernetes.io/name=employeeapp -n employeeapp
```

---

## 9. Accessing the Application

### Port forwarding (local access)

```powershell
# Forward application port
kubectl port-forward svc/employeeapp 8080:80 -n employeeapp

# Forward specific pod
kubectl port-forward <pod-name> 8080:8080 -n employeeapp

# Forward multiple ports
kubectl port-forward svc/employeeapp 8080:80 8443:443 -n employeeapp

# Forward in background (PowerShell)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "kubectl port-forward svc/employeeapp 8080:80 -n employeeapp"

# Access after port-forward
curl http://localhost:8080/api/employees
curl http://localhost:8080/swagger-ui.html
```

### Ingress access

```powershell
# Check ingress configuration
kubectl get ingress -n employeeapp

# Describe ingress
kubectl describe ingress employeeapp-ingress -n employeeapp

# Get ingress IP/hostname
kubectl get ingress employeeapp-ingress -n employeeapp -o jsonpath='{.status.loadBalancer.ingress[0].ip}'

# Test ingress endpoint
curl http://employeeapp.example.com/api/employees
```

### Exec into running pod

```powershell
# Shell into a pod
kubectl exec -it <pod-name> -n employeeapp -- /bin/sh

# Run a command inside a pod
kubectl exec -it <pod-name> -n employeeapp -- cat /app/config/application.properties

# Copy files from pod
kubectl cp employeeapp:<pod-name>:/app/logs/log.txt ./log.txt -n employeeapp

# Copy files to pod
kubectl cp ./config.properties employeeapp:<pod-name>:/tmp/config.properties -n employeeapp
```

---

## 10. Database Management

### MySQL pod access

```powershell
# Check MySQL pod status
kubectl get pods -l app.kubernetes.io/name=mysql -n employeeapp

# Shell into MySQL pod
kubectl exec -it <mysql-pod-name> -n employeeapp -- bash

# Run SQL query
kubectl exec -it <mysql-pod-name> -n employeeapp -- mysql -u root -prootpassword123 employeedb -e "SHOW TABLES;"

# Run SQL from file
kubectl exec -it <mysql-pod-name> -n employeeapp -- mysql -u root -prootpassword123 employeedb < schema.sql

# Port-forward MySQL for local access
kubectl port-forward svc/mysql 3306:3306 -n employeeapp
```

### Database connectivity test

```powershell
# Test from app pod
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://mysql:3306/

# Create debug pod and test
kubectl run debug-mysql --image=mysql:8.0 --rm -it --restart=Never -- mysql -h mysql -u root -prootpassword123 -e "SELECT 1" -n employeeapp

# Check MySQL logs
kubectl logs -l app.kubernetes.io/name=mysql -n employeeapp --tail=50
```

### Database backup

```powershell
# Backup database to a file
kubectl exec <mysql-pod-name> -n employeeapp -- mysqldump -u root -prootpassword123 employeedb > backup.sql

# Backup with timestamp
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
kubectl exec <mysql-pod-name> -n employeeapp -- mysqldump -u root -prootpassword123 employeedb > "backup_$timestamp.sql"

# Restore from backup
Get-Content backup.sql | kubectl exec -i <mysql-pod-name> -n employeeapp -- mysql -u root -prootpassword123 employeedb
```

### PVC management

```powershell
# Check PVC status
kubectl get pvc -n employeeapp

# Describe PVC
kubectl describe pvc mysql-pvc -n employeeapp

# Check PV binding
kubectl get pv

# Check storage class
kubectl get storageclass
```

---

## 11. Ingress & Networking

### Ingress management

```powershell
# Create ingress
kubectl apply -f kubernetes/ingress.yaml

# Check ingress
kubectl get ingress -n employeeapp

# Describe ingress
kubectl describe ingress employeeapp-ingress -n employeeapp

# Update ingress host
kubectl patch ingress employeeapp-ingress -n employeeapp -p '{"spec":{"rules":[{"host":"newdomain.com","http":{"paths":[{"path":"/","pathType":"Prefix","backend":{"service":{"name":"employeeapp","port":{"number":80}}}]}}]}}'

# Delete ingress
kubectl delete ingress employeeapp-ingress -n employeeapp
```

### Network debugging

```powershell
# Check service endpoints
kubectl get endpoints -n employeeapp

# Describe service
kubectl describe svc employeeapp -n employeeapp

# Test DNS resolution inside cluster
kubectl run dns-test --image=busybox --rm -it --restart=Never -- nslookup employeeapp -n employeeapp

# Test connectivity between pods
kubectl exec -it <pod-name> -n employeeapp -- ping mysql

# Check network policies
kubectl get networkpolicy -n employeeapp
```

### TLS/SSL management

```powershell
# Create TLS secret from certificate
kubectl create secret tls employeeapp-tls-secret --cert=tls.crt --key=tls.key -n employeeapp

# Check TLS secret
kubectl get secret employeeapp-tls-secret -n employeeapp

# Self-signed certificate for development (using OpenSSL)
# openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout tls.key -out tls.crt -subj "/CN=employeeapp.example.com"
```

---

## 12. Troubleshooting

### Pod not starting

```powershell
# Check pod status
kubectl get pods -n employeeapp

# Describe pod for events and errors
kubectl describe pod <pod-name> -n employeeapp

# Check logs
kubectl logs <pod-name> -n employeeapp

# Check previous container logs (if crashed)
kubectl logs <pod-name> -n employeeapp --previous

# Check init container logs
kubectl logs <pod-name> -n employeeapp -c <init-container-name>
```

### Image pull issues

```powershell
# Check image pull status
kubectl describe pod <pod-name> -n employeeapp | Select-String "Pulling|Pulled|Error"

# Check image exists
docker images | Select-String "employeeapp"

# Force pull latest image
kubectl set image deployment/employeeapp employeeapp=employeeapp:latest -n employeeapp --record

# Check image pull secrets
kubectl get secret -n employeeapp
```

### CrashLoopBackOff

```powershell
# Check why pod is crashing
kubectl describe pod <pod-name> -n employeeapp

# Check logs before crash
kubectl logs <pod-name> -n employeeapp --tail=200

# Check previous crash logs
kubectl logs <pod-name> -n employeeapp --previous --tail=200

# Restart the pod
kubectl delete pod <pod-name> -n employeeapp
```

### Database connection issues

```powershell
# Check MySQL is running
kubectl get pods -l app.kubernetes.io/name=mysql -n employeeapp

# Check MySQL logs
kubectl logs -l app.kubernetes.io/name=mysql -n employeeapp --tail=100

# Test MySQL connectivity from app
kubectl exec -it <pod-name> -n employeeapp -- ping mysql

# Check MySQL service
kubectl get svc mysql -n employeeapp
kubectl describe svc mysql -n employeeapp

# Check MySQL endpoints
kubectl get endpoints mysql -n employeeapp
```

### Application health check failing

```powershell
# Test health endpoint manually
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/actuator/health

# Check application logs for startup errors
kubectl logs <pod-name> -n employeeapp --tail=200 | Select-String "ERROR|Exception|Failed"

# Check readiness probe configuration
kubectl describe pod <pod-name> -n employeeapp | Select-String "Readiness"
```

### Resource issues (OOMKilled, CPU throttling)

```powershell
# Check resource usage
kubectl top pods -n employeeapp

# Check for OOMKilled
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.status.containerStatuses[*].lastState.terminated.reason}{"\n"}{end}'

# Increase resource limits
kubectl set resources deployment/employeeapp -c=employeeapp --limits=memory=2Gi,cpu=1000m -n employeeapp

# Check resource quotas
kubectl get resourcequota -n employeeapp
```

### General debugging

```powershell
# Create a debug pod in the same namespace
kubectl run debug-pod --image=busybox --rm -it --restart=Never -- /bin/sh

# Run curl from debug pod
kubectl run debug-curl --image=curlimages/curl --rm -it --restart=Never -- curl http://employeeapp:80/api/employees -n employeeapp

# Check all events
kubectl get events -n employeeapp --sort-by='.lastTimestamp'

# Check DNS resolution
kubectl run dns-debug --image=busybox --rm -it --restart=Never -- nslookup employeeapp -n employeeapp
```

---

## 13. Maintenance

### Rolling restart

```powershell
# Rolling restart all pods
kubectl rollout restart deployment/employeeapp -n employeeapp

# Monitor restart
kubectl rollout status deployment/employeeapp -n employeeapp

# Rolling restart MySQL
kubectl rollout restart deployment/mysql -n employeeapp
```

### Pod management

```powershell
# Delete a specific pod (deployment will recreate it)
kubectl delete pod <pod-name> -n employeeapp

# Delete all app pods (rolling restart)
kubectl delete pods -l app.kubernetes.io/name=employeeapp -n employeeapp

# Force delete a stuck pod
kubectl delete pod <pod-name> -n employeeapp --force --grace-period=0

# Cordon a node (prevent scheduling)
kubectl cordon <node-name>

# Uncordon a node (allow scheduling)
kubectl uncordon <node-name>

# Drain a node (safely evict pods)
kubectl drain <node-name> --ignore-daemonsets --delete-emptydir-data
```

### Resource limits update

```powershell
# Update resource requests and limits
kubectl set resources deployment/employeeapp -c=employeeapp --requests=memory=512Mi,cpu=250m --limits=memory=1Gi,cpu=500m -n employeeapp

# Update MySQL resources
kubectl set resources deployment/mysql -c=mysql --requests=memory=512Mi,cpu=250m --limits=memory=1Gi,cpu=500m -n employeeapp

# Verify resource settings
kubectl describe deployment employeeapp -n employeeapp | Select-String "Limits|Requests"
```

### Label and annotation management

```powershell
# Add label
kubectl label deployment/employeeapp environment=production -n employeeapp --overwrite

# Add annotation
kubectl annotate deployment/employeeapp description="Employee Management API" -n employeeapp --overwrite

# Remove label
kubectl label deployment/employeeapp environment- -n employeeapp

# Get resources by label
kubectl get pods -l app.kubernetes.io/name=employeeapp -n employeeapp
```

---

## 14. Cleanup

### Selective cleanup

```powershell
# Delete HPA only
kubectl delete hpa employeeapp-hpa -n employeeapp

# Delete Ingress only
kubectl delete ingress employeeapp-ingress -n employeeapp

# Delete application only (keep database)
kubectl delete -f kubernetes/deployment.yaml
kubectl delete -f kubernetes/service.yaml

# Delete database only (keep application)
kubectl delete -f kubernetes/mysql-deployment.yaml
kubectl delete -f kubernetes/mysql-service.yaml
```

### Full cleanup

```powershell
# Delete all resources in the correct order
kubectl delete -f kubernetes/

# Or delete the entire namespace (and everything in it)
kubectl delete namespace employeeapp

# Verify deletion
kubectl get namespace employeeapp
kubectl get all -n employeeapp

# Delete persistent volume claim (data will be lost!)
kubectl delete pvc mysql-pvc -n employeeapp

# Delete persistent volume (if statically provisioned)
kubectl delete pv <pv-name>
```

### Cleanup orphaned resources

```powershell
# Check for orphaned PVCs
kubectl get pvc -n employeeapp

# Check for orphaned PVs
kubectl get pv

# Check for orphaned services
kubectl get svc -n employeeapp

# Check for orphaned secrets
kubectl get secret -n employeeapp
```

---

## 15. Minikube Local Development

### Start Minikube

```powershell
# Start with recommended resources
minikube start --cpus=4 --memory=8192

# Start with Docker driver
minikube start --driver=docker

# Start with specific Kubernetes version
minikube start --kubernetes-version=v1.28.0
```

### Enable required addons

```powershell
minikube addons enable ingress
minikube addons enable metrics-server
minikube addons enable storage-provisioner
```

### Build and deploy

```powershell
# Build image inside Minikube
minikube image build -t employeeapp:latest .

# Deploy all manifests
kubectl apply -f kubernetes/

# Wait for deployment
kubectl wait --for=condition=available deployment/employeeapp -n employeeapp --timeout=120s
```

### Access the application

```powershell
# Open service in browser
minikube service employeeapp -n employeeapp

# Get service URL
minikube service employeeapp -n employeeapp --url

# Port forward
kubectl port-forward svc/employeeapp 8080:80 -n employeeapp

# Access Minikube dashboard
minikube dashboard
```

### Minikube management

```powershell
# Check status
minikube status

# Stop Minikube
minikube stop

# Delete Minikube (reset everything)
minikube delete

# Update Minikube
minikube update

# View logs
minikube logs
```

---

## 16. Production Commands

### Multi-cluster management

```powershell
# List contexts
kubectl config get-contexts

# Switch context
kubectl config use-context <context-name>

# Set context for current session
kubectl --context=<context-name> get pods -n employeeapp
```

### GitOps-style deployment

```powershell
# Dry-run before applying
kubectl apply -f kubernetes/ --dry-run=server

# Diff before applying (requires kubectl diff plugin)
kubectl diff -f kubernetes/

# Apply with server-side apply
kubectl apply -f kubernetes/ --server-side

# Track last applied configuration
kubectl apply -f kubernetes/ --record
```

### Security hardening

```powershell
# Check for pods running as root
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.containers[*].securityContext.runAsUser}{"\n"}{end}'

# Check for privileged pods
kubectl get pods -n employeeapp -o jsonpath='{range .items[*]}{.metadata.name}{"\t"}{.spec.containers[*].securityContext.privileged}{"\n"}{end}'

# Create network policy (example)
# kubectl apply -f network-policy.yaml
```

### Backup and restore

```powershell
# Export all resources
kubectl get all -n employeeapp -o yaml > backup-resources.yaml
kubectl get configmap -n employeeapp -o yaml > backup-configmaps.yaml
kubectl get secret -n employeeapp -o yaml > backup-secrets.yaml

# Restore resources
kubectl apply -f backup-resources.yaml
kubectl apply -f backup-configmaps.yaml
kubectl apply -f backup-secrets.yaml
```

### Monitoring setup (Prometheus + Grafana)

```powershell
# Install Prometheus operator
kubectl create namespace monitoring
kubectl apply -f https://raw.githubusercontent.com/prometheus-operator/prometheus-operator/main/bundle.yaml

# Check metrics endpoint
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/actuator/prometheus
```

---

## Quick Reference Card

| Task | Command |
|---|---|
| Deploy all | `kubectl apply -f kubernetes/` |
| Check pods | `kubectl get pods -n employeeapp` |
| Scale to 5 | `kubectl scale deployment/employeeapp --replicas=5 -n employeeapp` |
| View logs | `kubectl logs -l app.kubernetes.io/name=employeeapp -n employeeapp -f` |
| Port forward | `kubectl port-forward svc/employeeapp 8080:80 -n employeeapp` |
| Update image | `kubectl set image deployment/employeeapp employeeapp=employeeapp:v1.1 -n employeeapp` |
| Rollback | `kubectl rollout undo deployment/employeeapp -n employeeapp` |
| Rolling restart | `kubectl rollout restart deployment/employeeapp -n employeeapp` |
| Check HPA | `kubectl get hpa -n employeeapp` |
| Resource usage | `kubectl top pods -n employeeapp` |
| Delete all | `kubectl delete namespace employeeapp` |
| Watch pods | `kubectl get pods -n employeeapp -w` |
| Describe pod | `kubectl describe pod <pod-name> -n employeeapp` |
| Exec into pod | `kubectl exec -it <pod-name> -n employeeapp -- /bin/sh` |
