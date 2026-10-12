# Kubernetes Deployment Guide

This guide explains how to deploy the Employee Management Application to a Kubernetes cluster.

> **📖 Complete kubectl Commands Reference**: See [`KUBECTL_COMMANDS.md`](./KUBECTL_COMMANDS.md) for an exhaustive list of all kubectl commands covering deployment, scaling, monitoring, troubleshooting, and more.
>
> **📊 Monitoring with Grafana & Prometheus**: See [`GRAFANA_PROMETHEUS.md`](./GRAFANA_PROMETHEUS.md) for the complete monitoring stack setup including metrics collection, dashboards, alerting, and troubleshooting.

## Prerequisites

- Kubernetes cluster (minikube, kind, EKS, GKE, AKS, etc.)
- `kubectl` configured and connected to your cluster
- Docker installed (for building images)
- NGINX Ingress Controller installed (for Ingress)
- Metrics Server installed (for HPA)

## Directory Structure

```
kubernetes/
├── README.md                       # This file — deployment guide
├── KUBECTL_COMMANDS.md             # Complete kubectl commands reference
├── GRAFANA_PROMETHEUS.md           # Monitoring stack documentation
├── namespace.yaml                  # Kubernetes namespace
├── secret.yaml                     # Database credentials
├── configmap.yaml                  # Application configuration
├── mysql-pvc.yaml                  # Persistent volume claim for MySQL
├── mysql-deployment.yaml           # MySQL database deployment
├── mysql-service.yaml              # MySQL service
├── deployment.yaml                 # Application deployment
├── service.yaml                    # Application service
├── ingress.yaml                    # Ingress for external access
├── hpa.yaml                        # Horizontal Pod Autoscaler
├── prometheus-configmap.yaml       # Prometheus scrape configuration
├── prometheus-rbac.yaml            # Prometheus RBAC (ServiceAccount, Role, RoleBinding)
├── prometheus-deployment.yaml      # Prometheus deployment
├── prometheus-service.yaml         # Prometheus service
├── grafana-configmap.yaml          # Grafana config + admin secret
├── grafana-deployment.yaml         # Grafana deployment
├── grafana-service.yaml            # Grafana service
├── grafana-pvc.yaml                # Grafana persistent storage
├── grafana-datasources.yaml        # Prometheus datasource provisioning
├── grafana-dashboards-configmap.yaml # Dashboard provider configuration
└── grafana-dashboards/
    └── spring-boot-dashboard.json  # Spring Boot metrics dashboard
```

## Quick Start

### 1. Build and Tag Docker Image

```bash
# Build the Docker image
docker build -t employeeapp:latest .

# If using minikube, load the image directly
minikube image build -t employeeapp:latest .
```

### 2. Deploy to Kubernetes

```bash
# Create namespace
kubectl apply -f kubernetes/namespace.yaml

# Apply secrets (update credentials first!)
kubectl apply -f kubernetes/secret.yaml

# Apply configuration
kubectl apply -f kubernetes/configmap.yaml

# Deploy MySQL database
kubectl apply -f kubernetes/mysql-pvc.yaml
kubectl apply -f kubernetes/mysql-deployment.yaml
kubectl apply -f kubernetes/mysql-service.yaml

# Deploy the application
kubectl apply -f kubernetes/deployment.yaml
kubectl apply -f kubernetes/service.yaml

# Apply Ingress (optional, requires NGINX Ingress Controller)
kubectl apply -f kubernetes/ingress.yaml

# Apply HPA (optional, requires Metrics Server)
kubectl apply -f kubernetes/hpa.yaml

# Deploy Monitoring Stack (optional — Prometheus + Grafana)
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

Or apply all at once:

```bash
kubectl apply -f kubernetes/
```

### 3. Verify Deployment

```bash
# Check namespace
kubectl get namespaces | grep employeeapp

# Check pods
kubectl get pods -n employeeapp

# Check services
kubectl get svc -n employeeapp

# Check deployment
kubectl get deployment -n employeeapp

# Check HPA
kubectl get hpa -n employeeapp

# Check ingress
kubectl get ingress -n employeeapp

# Check monitoring pods
kubectl get pods -n employeeapp | grep -E "prometheus|grafana"
```

### 4. Access the Application

#### Using Port Forwarding (Local Development)

```bash
# Forward the application service
kubectl port-forward svc/employeeapp 8080:80 -n employeeapp

# Access the application
curl http://localhost:8080/api/employees

# Access Swagger UI
open http://localhost:8080/swagger-ui.html

# Forward Prometheus
kubectl port-forward svc/prometheus 9090:9090 -n employeeapp
# Open: http://localhost:9090

# Forward Grafana
kubectl port-forward svc/grafana 3000:3000 -n employeeapp
# Open: http://localhost:3000 (login: admin/admin)
```

#### Using Ingress (Production)

Update the `ingress.yaml` file with your domain and configure DNS:

```yaml
spec:
  rules:
    - host: employeeapp.yourdomain.com
```

## Configuration

### Environment Variables

The application uses environment variables for configuration, injected via ConfigMap and Secret:

| Variable | Source | Description |
|----------|--------|-------------|
| `SPRING_PROFILES_ACTIVE` | ConfigMap | Active Spring profile (default: `prod`) |
| `DB_HOST` | ConfigMap | Database host (default: `mysql`) |
| `DB_PORT` | ConfigMap | Database port (default: `3306`) |
| `DB_NAME` | ConfigMap | Database name (default: `employeedb`) |
| `DB_USERNAME` | Secret | Database username |
| `DB_PASSWORD` | Secret | Database password |
| `JAVA_OPTS` | ConfigMap | JVM options |

### Updating Configuration

```bash
# Edit ConfigMap
kubectl edit configmap employeeapp-config -n employeeapp

# Edit Secret
kubectl edit secret employeeapp-secret -n employeeapp

# Restart deployment after config change
kubectl rollout restart deployment/employeeapp -n employeeapp
```

## Scaling

### Manual Scaling

```bash
# Scale to 5 replicas
kubectl scale deployment/employeeapp --replicas=5 -n employeeapp
```

### Auto Scaling (HPA)

The HPA is configured to:
- Scale between 2 and 10 replicas
- Target 70% CPU utilization
- Target 80% memory utilization
- Scale up gradually (max 2 pods per minute)
- Scale down slowly (1 pod per 2 minutes, 5-minute stabilization)

```bash
# Check HPA status
kubectl get hpa -n employeeapp

# Describe HPA for details
kubectl describe hpa employeeapp-hpa -n employeeapp
```

## Monitoring

### View Logs

```bash
# View all logs
kubectl logs -l app.kubernetes.io/name=employeeapp -n employeeapp -f

# View specific pod logs
kubectl logs <pod-name> -n employeeapp -f

# View MySQL logs
kubectl logs -l app.kubernetes.io/name=mysql -n employeeapp -f
```

### Check Pod Status

```bash
# Describe a pod for detailed status
kubectl describe pod <pod-name> -n employeeapp

# Check events
kubectl get events -n employeeapp --sort-by='.lastTimestamp'
```

## Updates and Rollouts

### Deploy New Version

```bash
# Build new image
docker build -t employeeapp:v1.1 .

# Update deployment image
kubectl set image deployment/employeeapp employeeapp=employeeapp:v1.1 -n employeeapp

# Monitor rollout
kubectl rollout status deployment/employeeapp -n employeeapp
```

### Rollback

```bash
# View rollout history
kubectl rollout history deployment/employeeapp -n employeeapp

# Rollback to previous version
kubectl rollout undo deployment/employeeapp -n employeeapp

# Rollback to specific revision
kubectl rollout undo deployment/employeeapp --to-revision=2 -n employeeapp
```

## Production Checklist

- [ ] Update `secret.yaml` with strong, unique database credentials
- [ ] Configure TLS certificate for Ingress (use cert-manager for automatic SSL)
- [ ] Set up proper PersistentVolume storage class for your cluster
- [ ] Configure resource limits appropriate for your workload
- [ ] Set up monitoring (Prometheus + Grafana)
- [ ] Configure log aggregation (ELK, Fluentd, etc.)
- [ ] Set up backup strategy for MySQL data
- [ ] Configure network policies for security
- [ ] Use sealed-secrets or external secret manager (Vault, AWS Secrets Manager)
- [ ] Set up CI/CD pipeline for automated deployments

## Cleanup

```bash
# Delete all resources
kubectl delete -f kubernetes/

# Or delete the entire namespace
kubectl delete namespace employeeapp
```

## Troubleshooting

### Pod Not Starting

```bash
# Check pod events
kubectl describe pod <pod-name> -n employeeapp

# Check logs
kubectl logs <pod-name> -n employeeapp
```

### Database Connection Issues

```bash
# Check MySQL pod status
kubectl get pods -l app.kubernetes.io/name=mysql -n employeeapp

# Check MySQL logs
kubectl logs -l app.kubernetes.io/name=mysql -n employeeapp

# Test database connectivity
kubectl run -it --rm debug --image=mysql:8.0 --restart=Never -- mysql -h mysql -u root -prootpassword123 -e "SELECT 1" -n employeeapp
```

### Application Health Check Failing

```bash
# Check actuator health endpoint
kubectl exec -it <pod-name> -n employeeapp -- wget -qO- http://localhost:8080/actuator/health

# Check application logs
kubectl logs <pod-name> -n employeeapp --tail=100
```

## Local Development with Minikube

```bash
# Start minikube
minikube start --cpus=4 --memory=8192

# Enable addons
minikube addons enable ingress
minikube addons enable metrics-server

# Build and load image
minikube image build -t employeeapp:latest .

# Deploy
kubectl apply -f kubernetes/

# Access the application
minikube service employeeapp -n employeeapp
```
