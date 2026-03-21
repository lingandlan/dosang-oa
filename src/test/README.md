# OA 系统集成测试

本目录包含 OA 系统的集成测试用例，用于测试各微服务的 API 接口。

## 测试目录结构

```
src/test/java/com/openoa/integration/
├── BaseIntegrationTest.java           # 测试基类，提供通用辅助方法
├── UserServiceIntegrationTest.java    # 用户服务集成测试
├── ApprovalServiceIntegrationTest.java # 审批服务集成测试
├── NoticeServiceIntegrationTest.java  # 公告服务集成测试
├── VideoServiceIntegrationTest.java  # 视频服务集成测试
└── OAE2EIntegrationTest.java         # 端到端业务流程测试

src/test/resources/
└── application-test.yml              # 测试环境配置文件
```

## 测试覆盖的 API 接口

### 1. 用户服务 (user-service: 8081)

| 接口路径 | 方法 | 测试用例 |
|---------|------|---------|
| /api/v1/auth/register | POST | 用户注册成功/用户名已存在 |
| /api/v1/auth/login | POST | 用户登录成功/密码错误 |
| /api/v1/users/{id} | GET | 获取用户信息成功/用户不存在 |
| /api/v1/users | GET | 获取用户列表/按用户名搜索 |
| /api/v1/departments | GET/POST | 部门列表/创建部门 |
| /api/v1/departments/{id} | GET | 部门详情 |
| /api/v1/departments/tree | GET | 部门树形结构 |

### 2. 审批服务 (approval-service: 8082)

| 接口路径 | 方法 | 测试用例 |
|---------|------|---------|
| /api/v1/approvals | POST/GET | 提交审批/审批列表 |
| /api/v1/approvals/{id} | GET | 审批详情 |
| /api/v1/approvals/{id} | PUT | 审批通过/审批拒绝 |
| /api/v1/approvals/types | GET | 审批类型列表 |
| /api/v1/approvals/approver/{id} | GET | 待审批列表 |

### 3. 公告服务 (notice-service: 8084)

| 接口路径 | 方法 | 测试用例 |
|---------|------|---------|
| /api/v1/notices | POST/GET | 发布公告/公告列表 |
| /api/v1/notices/{id} | GET | 公告详情 |
| /api/v1/notices/{id} | PUT | 更新公告 |
| /api/v1/notices/{id}/publish | PUT | 发布公告 |
| /api/v1/notices/{id} | DELETE | 删除公告 |

### 4. 视频服务 (video-service: 8085)

| 接口路径 | 方法 | 测试用例 |
|---------|------|---------|
| /api/v1/video/rooms | POST/GET | 创建房间/房间列表 |
| /api/v1/video/rooms/{id} | GET | 房间详情 |
| /api/v1/video/rooms/join | POST | 加入房间 |
| /api/v1/video/rooms/{id}/leave | POST | 离开房间 |
| /api/v1/video/rooms/{id}/participants | GET | 获取参与者列表 |
| /api/v1/video/rooms/{id}/kick/{userId} | POST | 踢出参与者 |
| /api/v1/video/config | GET | 获取视频配置 |
| /api/v1/video/health | GET | 健康检查 |

## 运行测试

### 运行所有集成测试

```bash
mvn test
```

### 运行单个测试类

```bash
# 用户服务测试
mvn test -Dtest=UserServiceIntegrationTest

# 审批服务测试
mvn test -Dtest=ApprovalServiceIntegrationTest

# 公告服务测试
mvn test -Dtest=NoticeServiceIntegrationTest

# 视频服务测试
mvn test -Dtest=VideoServiceIntegrationTest

# 端到端测试
mvn test -Dtest=OAE2EIntegrationTest
```

### 运行指定测试方法

```bash
mvn test -Dtest=UserServiceIntegrationTest#testRegister_Success
```

## 测试配置

测试使用 H2 内存数据库，配置文件位于 `src/test/resources/application-test.yml`。

### 主要配置项

- `spring.datasource.url`: H2 内存数据库
- `server.port`: 随机端口 (0)
- `spring.jpa.hibernate.ddl-auto`: create-drop (自动创建表结构)

## 测试说明

1. **随机端口**: 测试使用 `RANDOM_PORT` 模式，每个测试实例使用随机可用端口
2. **独立数据**: 每个测试方法应该独立，不依赖其他测试的执行顺序
3. **断言策略**: 使用 RESTful API 的标准响应格式 `{code: 200, message: "success", data: {...}}`
4. **错误处理**: 考虑了各种错误场景，如用户不存在、密码错误等

## 注意事项

1. 确保各微服务已正确配置和启动
2. 测试需要数据库支持，默认使用 H2 内存数据库
3. 部分测试可能需要预先存在的基础数据（如管理员用户）
4. 端到端测试按 Order 顺序执行，用于测试完整的业务流程
