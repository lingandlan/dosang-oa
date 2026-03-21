# OA 系统部署指南

## 快速开始

### 本地开发 (Docker Compose)

```bash
# 1. 启动所有服务
docker-compose up -d

# 2. 查看日志
docker-compose logs -f

# 3. 停止服务
docker-compose down
```

### 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost |
| API 网关 | http://localhost:8080 |
| Eureka | http://localhost:8761 |
| MySQL | localhost:3306 |
| Redis | localhost:6379 |

---

## 生产部署 (K8s)

### 前置要求

- Kubernetes 集群
- kubectl 配置完成
- Docker 镜像仓库

### 步骤

```bash
# 1. 构建镜像
docker build -t oa-gateway:latest ./gateway
# ... 其他服务

# 2. 推送到镜像仓库
docker push your-registry/oa-gateway:latest

# 3. 部署到 K8s
kubectl apply -k k8s/base/

# 4. 查看状态
kubectl get pods -n oa-system
```

### CI/CD 部署

推送代码到 `main` 分支自动触发部署：
- GitHub Actions 自动构建镜像
- 自动部署到 K8s

---

## 环境变量

| 变量 | 说明 | 默认值 |
|------|------|--------|
| DB_HOST | 数据库地址 | mysql |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名称 | oa_system |
| DB_USER | 数据库用户 | root |
| DB_PASSWORD | 数据库密码 | 111111 |
| REDIS_HOST | Redis 地址 | redis |
| REDIS_PORT | Redis 端口 | 6379 |
