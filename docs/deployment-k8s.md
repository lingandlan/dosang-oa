# OA 系统 Kubernetes 部署方案

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                         K8s Cluster                         │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐       │
│  │  Ingress │  │ Gateway │  │ Eureka  │  │ MySQL   │       │
│  │  (Nginx) │  │ 8080    │  │ 8761    │  │ 3306    │       │
│  └─────────┘  └─────────┘  └─────────┘  └─────────┘       │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│  │  user    │ │ approval │ │ notice   │ │ video    │       │
│  │ service  │ │ service  │ │ service  │ │ service  │       │
│  │ 8081    │ │ 8082    │ │ 8084    │ │ 8085    │       │
│  └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────┐ ┌──────────┐                                 │
│  │Redis     │ │Frontend  │                                 │
│  │6379      │ │(uniapp)  │                                 │
│  └──────────┘ └──────────┘                                 │
└─────────────────────────────────────────────────────────────┘
```

## K8s 资源清单

### 1. Deployment ( deployments/)

| 服务 | 副本数 | 镜像 | 端口 |
|------|--------|------|------|
| gateway | 2 | oa-gateway | 8080 |
| eureka-server | 2 | oa-eureka | 8761 |
| user-service | 3 | oa-user | 8081 |
| approval-service | 3 | oa-approval | 8082 |
| notice-service | 3 | oa-notice | 8084 |
| video-service | 3 | oa-video | 8085 |
| frontend | 2 | oa-frontend | 80 |

### 2. Service (services/)

- ClusterIP: 内部服务通信
- NodePort: 外部访问（开发环境）
- LoadBalancer: 生产环境

### 3. Ingress

- 域名: oa.yourcompany.com
- SSL 终端
- 路径路由

### 4. ConfigMap / Secret

- 数据库配置
- Redis 配置
- 应用配置

### 5. PersistentVolume

- MySQL 数据存储
- 日志存储

## 部署清单

```bash
# 1. 构建镜像
docker build -t oa-gateway:latest ./gateway
docker build -t oa-user:latest ./user-service
# ... 其他服务

# 2. 推送到镜像仓库
docker push registry.example.com/oa-gateway:latest

# 3. 部署到 K8s
kubectl apply -f k8s/

# 4. 查看状态
kubectl get pods -n oa-system
kubectl get svc -n oa-system
```

## 环境要求

### 开发/测试环境
- Minikube / Kind (单节点)
- 4核 CPU / 8GB 内存

### 生产环境
- K8s 集群 (至少 3 节点)
- 16核 CPU / 32GB 内存
- SSD 存储

## 下一步

1. 确认 K8s 集群环境
2. 编写 Dockerfile
3. 编写 K8s 部署 YAML
4. 配置 CI/CD 流水线
