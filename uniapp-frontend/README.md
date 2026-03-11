# OpenOA - uniapp 前端

企业办公平台 uniapp 前端项目

## 项目结构

```
uniapp-frontend/
├── pages/                 # 页面
│   ├── login/            # 登录页
│   ├── home/             # 首页
│   ├── approval/         # 审批
│   ├── attendance/       # 考勤打卡
│   └── notice/           # 公告
├── static/               # 静态资源
│   └── common.css        # 通用样式
├── utils/                # 工具
│   └── api.js            # API 接口
├── App.vue               # 应用入口
├── main.js               # 主入口
├── pages.json            # 页面配置
└── package.json          # 依赖配置
```

## 功能模块

### 1. 登录页面
- 用户名密码登录
- 记住密码功能
- 快捷登录（微信、QQ、钉钉）
- 忘记密码提示

### 2. 首页
- 用户信息展示
- 快捷统计（待审批、未读公告、工作天数）
- 功能导航（审批、考勤、公告、视频会议、日程、文档）
- 快捷打卡
- 最新公告列表

### 3. 审批页面
- 待审批、已审批、我的申请三个标签
- 审批列表展示
- 审批详情查看
- 同意/拒绝操作
- 创建新审批

### 4. 考勤打卡页面
- 实时时间显示
- 上班/下班打卡
- 打卡记录日历视图
- 月度统计（工作天数、迟到、早退、加班）
- 打卡详情查看

### 5. 公告列表页面
- 公告列表展示
- 分类标签（公司、部门、个人、紧急）
- 搜索功能
- 上拉加载更多
- 下拉刷新
- 公告详情查看

## 开发说明

### 安装依赖

```bash
npm install
```

### 开发运行

H5 端：
```bash
npm run dev:h5
```

微信小程序：
```bash
npm run dev:mp-weixin
```

APP 端：
```bash
npm run dev:app
```

### 打包构建

H5 端：
```bash
npm run build:h5
```

微信小程序：
```bash
npm run build:mp-weixin
```

APP 端：
```bash
npm run build:app
```

## API 接口配置

修改 `utils/api.js` 中的 `BASE_URL` 配置后端接口地址：

```javascript
const BASE_URL = 'http://localhost:8080'
```

## 注意事项

1. 确保后端服务正常运行
2. 根据实际后端接口调整 API 请求参数
3. 首次运行需要先登录获取 token
4. 部分功能需要相应的权限配置
