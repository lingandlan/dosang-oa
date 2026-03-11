# User Service API 文档

## 用户管理 API (User Service)

### 基础信息
- 服务端口: 8081
- 基础路径: `/api/v1/users`

---

## API 接口列表

### 1. 分页查询用户列表
```
GET /api/v1/users
```

**请求参数:**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页数量，默认 10 |
| username | String | 否 | 用户名（模糊查询） |
| realName | String | 否 | 真实姓名（模糊查询） |
| departmentId | Long | 否 | 部门ID |
| status | Integer | 否 | 状态（0-禁用，1-启用） |

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 2. 根据ID查询用户
```
GET /api/v1/users/{id}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "admin",
    "realName": "管理员",
    "email": "admin@example.com",
    ...
  }
}
```

---

### 3. 创建用户
```
POST /api/v1/users
Content-Type: application/json
```

**请求体:**
```json
{
  "username": "user1",
  "password": "123456",
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138002",
  "departmentId": 2,
  "roleId": 2
}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "创建成功",
  "data": { ... }
}
```

---

### 4. 更新用户
```
PUT /api/v1/users/{id}
Content-Type: application/json
```

**请求体:**
```json
{
  "username": "user1",
  "realName": "张三",
  "email": "zhangsan@example.com",
  "phone": "13800138002",
  "departmentId": 2,
  "roleId": 2,
  "status": 1
}
```

**注意:** 更新时可以不传 password 字段，不传则保留原密码

---

### 5. 删除用户
```
DELETE /api/v1/users/{id}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "删除成功"
}
```

---

### 6. 查询部门下的所有用户
```
GET /api/v1/users/department/{departmentId}
```

**响应示例:**
```json
{
  "code": 200,
  "message": "success",
  "data": [...]
}
```

---

### 7. 查询部门下的启用用户
```
GET /api/v1/users/department/{departmentId}/active
```

---

### 8. 更新用户状态
```
PUT /api/v1/users/{id}/status?status=0
```

**请求参数:**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| status | Integer | 是 | 状态（0-禁用，1-启用） |

---

### 9. 修改密码
```
POST /api/v1/users/{id}/password
Content-Type: application/json
```

**请求体:**
```json
{
  "oldPassword": "123456",
  "newPassword": "654321"
}
```

---

### 10. 重置密码
```
POST /api/v1/users/{id}/reset-password
Content-Type: application/json
```

**请求体:**
```json
{
  "newPassword": "123456"
}
```

---

### 11. 批量删除用户
```
DELETE /api/v1/users/batch
Content-Type: application/json
```

**请求体:**
```json
[1, 2, 3]
```

---

### 12. 批量更新用户状态
```
PUT /api/v1/users/batch/status
Content-Type: application/json
```

**请求体:**
```json
{
  "ids": [1, 2, 3],
  "status": 1
}
```

---

### 13. 检查用户名是否存在
```
GET /api/v1/users/check/username?username=user1&excludeId=1
```

**请求参数:**
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| excludeId | Long | 否 | 排除的用户ID |

**响应:**
```json
{
  "code": 200,
  "message": "success",
  "data": true  // true-不存在，false-已存在
}
```

---

### 14. 检查手机号是否存在
```
GET /api/v1/users/check/phone?phone=13800138002&excludeId=1
```

---

### 15. 检查邮箱是否存在
```
GET /api/v1/users/check/email?email=test@example.com&excludeId=1
```

---

## 数据库表结构

### sys_user (用户表)
| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 用户ID（主键） |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（BCrypt加密） |
| real_name | VARCHAR(50) | 真实姓名 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| department_id | BIGINT | 部门ID |
| role_id | BIGINT | 角色ID |
| status | TINYINT | 状态（0-禁用，1-启用） |
| avatar | VARCHAR(200) | 头像URL |
| remark | VARCHAR(500) | 备注 |
| deleted | TINYINT | 删除标记（逻辑删除） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

## 状态码说明
| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 使用说明

1. 密码使用 BCrypt 加密存储
2. 所有删除操作均为逻辑删除（deleted=1）
3. 创建用户时密码为必填项
4. 更新用户时密码为可选项，不传则保留原密码
5. 所有时间字段自动填充
