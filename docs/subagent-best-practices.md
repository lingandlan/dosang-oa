# Subagent 协同开发最佳实践

**创建日期**: 2026-03-11

---

## 一、实际开发方面

### 1.1 代码规范统一

```markdown
【强制要求】
- 命名规范: 英文驼峰命名
- 代码格式: 使用 IDE 默认格式化
- 注释: 核心逻辑必须有注释
- 提交信息: 英文，格式: "feat: 添加用户登录功能"
```

### 1.2 接口约定

```markdown
【REST API 规范】
- URL: /api/v1/{module}/{resource}
- 方法: GET(查询) / POST(新增) / PUT(更新) / DELETE(删除)
- 响应格式: { code: 200, data: {}, message: "success" }
- 错误码: 参照统一错误码文档

【示例】
GET    /api/v1/users           # 获取用户列表
POST   /api/v1/users           # 创建用户
GET    /api/v1/users/{id}     # 获取单个用户
PUT    /api/v1/users/{id}     # 更新用户
DELETE /api/v1/users/{id}     # 删除用户
```

### 1.3 数据库设计规范

```markdown
【表命名】
- 业务表: t_{module}_{entity}
- 字典表: t_dict_{category}
- 日志表: t_log_{module}

【字段命名】
- 通用字段: id, created_at, updated_at, created_by, updated_by
- 软删除: deleted_at (timestamp)

【示例】
t_user (用户表)
t_approval_instance (审批实例)
t_attendance_record (考勤记录)
```

### 1.4 日志规范

```markdown
【日志级别】
- ERROR: 错误，影响功能
- WARN: 警告，可能有问题
- INFO: 重要业务流程
- DEBUG: 调试信息

【格式】
[时间] [级别] [类名] [消息] [额外数据]
2026-03-11 10:00:00 INFO UserService login success userId=123
```

### 1.5 错误处理规范

```markdown
【统一异常处理】
- 使用全局异常处理器
- 区分业务异常和系统异常
- 不暴露敏感信息到前端

【示例】
- 业务异常: throw new BusinessException("用户名已存在", 400)
- 系统异常: throw new SystemException("数据库连接失败", 500)
```

---

## 二、项目管理方面

### 2.1 任务拆分粒度

```markdown
【建议】
- 单个任务: 2-4 小时工作量
- 任务描述: 包含目标、验收标准、技术要点
- 任务独立性: 尽量减少任务间的依赖

【任务模板】
## 任务: user-service 登录接口

### 目标
实现用户账号密码登录功能

### 验收标准
- [ ] 登录成功返回 JWT token
- [ ] 密码错误返回错误提示
- [ ] 账号不存在返回错误提示

### 技术要点
- 使用 Spring Security + JWT
- 密码使用 BCrypt 加密
```

### 2.2 进度同步机制

```markdown
【同步频率】
- 每日站会: 每人 3 分钟进度汇报
- 任务状态更新: 完成后立即更新
- 阻塞事项: 立即上报

【状态定义】
- 待开始 (TODO): 任务已创建
- 进行中 (IN_PROGRESS): 正在开发
- 待审核 (PENDING_REVIEW): 待代码审查
- 完成 (DONE): 已合并到主分支
- 阻塞 (BLOCKED): 遇到问题无法继续
```

### 2.3 代码整合流程

```markdown
【流程】
1. 子分支开发: feature/{task-id}-{模块}
2. 本地测试: 确保单元测试通过
3. 提交 Pull Request
4. 代码审查: 由 main agent 审查
5. 合并到主分支

【合并原则】
- 每日下班前必须合并当天代码
- 冲突由提交者负责解决
- 禁止强制合并
```

### 2.4 代码审查要点

```markdown
【审查清单】
- [ ] 代码符合规范
- [ ] 逻辑正确无 bug
- [ ] 有适当的单元测试
- [ ] 无敏感信息泄露
- [ ] 性能无明显问题
- [ ] 注释清晰完整
```

---

## 三、其他重要维度

### 3.1 沟通机制

```markdown
【沟通渠道】
- 飞书群: 项目日常沟通
- 文档: 重要决策记录
- 代码评论: 技术细节讨论

【升级机制】
- 阻塞 30 分钟 → 升级到 main agent
- 需求不清晰 → 立即询问
- 技术方案不确定 → 先讨论再开发
```

### 3.2 文档管理

```markdown
【文档结构】
docs/
├── plans/           # 设计方案
├── api/             # API 文档
├── database/        # 数据库文档
├── deploy/          # 部署文档
└── tasks.md        # 任务看板

【维护原则】
- 设计与开发同步更新
- 重大变更必须更新文档
- 文档 Review 纳入代码审查
```

### 3.3 测试策略

```markdown
【测试分层】
1. 单元测试: 每个方法必须测试 (覆盖率 > 70%)
2. 集成测试: 模块间接口测试
3. 端到端测试: 关键业务流程

【测试数据】
- 使用 Flyway 管理测试数据
- 测试环境独立数据库
- 每次测试重置数据
```

### 3.4 部署流程

```markdown
【环境】
- 开发环境: dev (日常开发)
- 测试环境: test (QA 测试)
- 预发布: pre (最终验收)
- 生产环境: prod (正式环境)

【部署方式】
- 容器化: Docker + K8s
- CI/CD: 自动构建测试部署
- 回滚: 支持一键回滚
```

---

## 四、Agent 协作注意事项

### 4.1 任务分配原则

```markdown
【分配策略】
- 按模块分配: 一个 agent 负责一个完整模块
- 按技能匹配: 后端 → agent-1~3, 前端 → agent-4
- 按工作量平衡: 尽量均匀分配
```

### 4.2 冲突处理

```markdown
【代码冲突】
- 优先沟通解决方案
- 必要时由 main agent 仲裁

【任务冲突】
- 明确任务边界
- 共享代码需要额外审查
```

### 4.3 质量保证

```markdown
【质量门禁】
- 单元测试通过
- 代码格式检查通过
- 无严重警告
- 代码审查通过
```

---

## 五、每日汇报模板

```markdown
📊 OA 系统开发日报 - 2026-03-11

【总体进度】Phase 1: 15%

【今日完成】
✅ agent-1: 完成父 POM 配置
✅ agent-1: 完成 Gateway 基础结构

【进行中】
🔄 agent-1: Nacos 配置 (60%)
🔄 agent-2: user-service 数据库设计 (40%)

【明日计划】
- 完成 Nacos 配置
- 开始 user-service CRUD 开发

【问题/阻塞】
⚠️ 需要确认: 数据库连接池配置参数

【代码统计】
- 新增: 150 行
- 修改: 30 行
- 删除: 0 行
```

---

## 六、紧急情况处理

```markdown
【阻塞升级】
- 阻塞 > 30 分钟 → 升级到 main agent
- 技术难题 → 升级到 main agent
- 需求变更 → 升级到 main agent

【回滚机制】
- 合并后 2 小时内可无条件回滚
- 严重问题一键回滚
- 回滚后 24 小时内修复
```

---

*本文档由 main agent 维护，供团队参考*
