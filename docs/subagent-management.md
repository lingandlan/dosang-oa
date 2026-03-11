# OA 系统 Subagent 开发管理方案

## 📋 项目概述

- **项目名称**: OpenOA 企业办公平台
- **开发方式**: 多 Agent 协同开发
- **创建日期**: 2026-03-11

---

## 🤖 Agent 配置

| Agent ID | 名称 | 职责 | 工作区 |
|----------|------|------|--------|
| agent-1 | 后端-用户服务 | 用户管理、权限控制 | workspace-agent-1 |
| agent-2 | 后端-审批考勤 | 审批流程、考勤打卡 | workspace-agent-2 |
| agent-3 | 后端-公告视频 | 公告通知、视频会议 | workspace-agent-3 |
| agent-4 | 前端-uniapp | 移动端前端开发 | workspace-agent-4 |
| qa | 测试工程师 | 测试验证 | workspace-qa |

---

## 📐 管理方案

### 模式：主协调者模式

我作为**主协调者 (main)**，统一指挥各个 subagent 进行开发。

```
main (主协调者)
├── agent-1 → user-service
├── agent-2 → approval-service + attendance-service  
├── agent-3 → notice-service + video-service + websocket-service
├── agent-4 → uniapp-frontend
└── qa → 测试验证
```

### 开发流程

```
1️⃣ 需求分析 → 2️⃣ 任务分配 → 3️⃣ 并行开发 → 4️⃣ 代码整合 → 5️⃣ 测试验证
```

| 阶段 | 执行方式 | 负责人 |
|------|----------|--------|
| 需求分析 | 我分析需求，设计架构 | main |
| 任务分配 | 我分配具体任务给各 agent | main |
| 并行开发 | exec + OpenCode 后台并行执行 | agent-1~4 |
| 代码整合 | 我检查代码、解决冲突 | main |
| 测试验证 | 运行测试用例 | qa |

---

## 🚀 使用方式

### 启动开发任务

```bash
# 并行启动多个 agent
exec pty:true background:true workdir:~/oa-system command:"opencode run '任务描述'"

# 或者使用 Claude Code
exec pty:true background:true workdir:~/project command:"claude '任务描述'"
```

### 查看进度

```bash
# 查看运行中的任务
process action:list

# 查看任务输出
process action:log sessionId:xxx
```

---

## 📁 项目结构

```
oa-system/
├── pom.xml                      # 父 POM
├── gateway/                      # 网关服务 (8080)
├── user-service/                # 用户服务 (8081)
├── approval-service/            # 审批服务 (8082)
├── attendance-service/          # 考勤服务 (8083)
├── notice-service/              # 公告服务 (8084)
├── video-service/               # 视频服务 (8085)
├── websocket-service/           # WebSocket 服务 (8086)
└── uniapp-frontend/             # 前端项目
```

---

## ⚠️ 注意事项

1. **Gateway 重启**：新增 agent 需要重启 Gateway 才能生效
   ```bash
   openclaw gateway restart
   ```

2. **agents_list**：当前配置可能需要检查 allowlist 才能使用 subagent

3. **代码整合**：各 agent 完成后需要我整合到统一分支

---

## 📝 任务记录

### 2026-03-11

- [x] 创建 4 个 subagent workspace
- [x] 配置 openclaw.json
- [x] 定义各 agent 职责
- [x] 制定管理方案

### 待办

- [ ] 启动第一个开发任务
- [ ] 完善 user-service
- [ ] 完善 approval-service
- [ ] 完善 attendance-service
- [ ] 完善 notice-service
- [ ] 完善 video-service
- [ ] 开发 uniapp 前端
- [ ] QA 测试

---

*本方案由 main agent 维护*
