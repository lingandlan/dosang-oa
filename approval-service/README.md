# Approval Service - Phase 2 完成文档

## 概述
approval-service 已完成 Phase 2 开发，新增了完整的审批流程引擎、模板管理、多级审批和通知集成功能。

## 新增功能

### 1. 审批流程引擎
- **核心类**: `ApprovalEngine`
- **功能**: 管理审批流程的整个生命周期
  - 启动流程
  - 审批通过
  - 审批拒绝
  - 审批退回
  - 撤回流程

### 2. 审批模板管理
- **实体**: `ApprovalTemplate`
- **API接口**:
  - `GET /api/v1/approval-templates` - 获取所有模板
  - `GET /api/v1/approval-templates/{id}` - 获取模板详情
  - `POST /api/v1/approval-templates` - 创建模板
  - `PUT /api/v1/approval-templates/{id}` - 更新模板
  - `DELETE /api/v1/approval-templates/{id}` - 删除模板

### 3. 多级审批节点
- **实体**: `ApprovalNode`
- **API接口**:
  - `GET /api/v1/approval-nodes/template/{templateId}` - 获取模板的所有节点
  - `POST /api/v1/approval-nodes` - 创建节点
  - `PUT /api/v1/approval-nodes/{id}` - 更新节点
  - `DELETE /api/v1/approval-nodes/{id}` - 删除节点

### 4. 审批通知集成
- **服务**: `ApprovalNotificationService`
- **功能**: 在审批流程的关键节点自动发送通知
  - 提交通知
  - 审批通过通知
  - 审批拒绝通知
  - 审批退回通知
  - 下一级审批人通知

## 数据库结构

### 新增表
1. **approval_template** - 审批模板表
2. **approval_node** - 审批节点表
3. **approval_record** - 审批记录表

### 扩展表
1. **approval_instance** - 新增字段:
   - `template_id` - 模板ID
   - `current_node_id` - 当前节点ID
   - `current_level` - 当前审批层级
   - `total_levels` - 总审批层级
   - `form_data` - 表单数据JSON

## 主要类说明

### 实体类
- `ApprovalTemplate` - 审批模板
- `ApprovalNode` - 审批节点
- `ApprovalRecord` - 审批记录
- `ApprovalInstance` - 审批实例（已扩展）

### 枚举类
- `ApprovalAction` - 审批动作 (SUBMIT, APPROVE, REJECT, RETURN, WITHDRAW)
- `ApprovalStatus` - 审批状态 (DRAFT, PENDING, APPROVED, REJECTED, RETURNED, CANCELLED)

### 服务类
- `ApprovalEngine` - 审批流程引擎核心
- `ApprovalTemplateService` - 模板管理服务
- `ApprovalProcessService` - 流程处理服务
- `ApprovalNotificationService` - 通知服务

### 客户端
- `NoticeClient` - 通知服务客户端 (Feign)

## 使用示例

### 创建审批模板
```bash
curl -X POST http://localhost:8083/api/v1/approval-templates \
  -H "Content-Type: application/json" \
  -d '{
    "name": "请假审批",
    "code": "LEAVE_APPROVAL",
    "description": "员工请假审批流程",
    "formConfig": "{}",
    "isActive": true
  }'
```

### 添加审批节点
```bash
curl -X POST http://localhost:8083/api/v1/approval-nodes \
  -H "Content-Type: application/json" \
  -d '{
    "templateId": 1,
    "name": "部门主管审批",
    "sequence": 1,
    "type": "APPROVE",
    "approverType": "ROLE",
    "approverConfig": "DEPT_MANAGER",
    "required": true
  }'
```

### 提交审批申请
```bash
curl -X POST http://localhost:8083/api/v1/approvals \
  -H "Content-Type: application/json" \
  -d '{
    "typeId": 1,
    "templateId": 1,
    "userId": 1,
    "title": "请假申请",
    "content": "因事请假一天",
    "formData": "{}"
  }'
```

### 审批操作
```bash
curl -X PUT "http://localhost:8083/api/v1/approvals/1?action=APPROVE&approverId=2&approverName=张三&comment=同意" \
  -H "Content-Type: application/json"
```

## 测试
运行单元测试：
```bash
mvn test
```

## 数据库初始化
执行以下SQL脚本初始化数据库：
1. `schema.sql` - 创建表结构
2. `data.sql` - 插入示例数据
