<template>
  <view class="profile-container">
    <view class="header">
      <view class="avatar">
        <text>{{ userInitial }}</text>
      </view>
      <text class="username">{{ userInfo.realName || userInfo.username }}</text>
      <text class="department">{{ userInfo.department || '技术部' }} · {{ userInfo.position || '员工' }}</text>
    </view>
    
    <view class="stats-card">
      <view class="stat-item" @tap="navigateTo('/pages/approval/approval?tab=my')">
        <text class="stat-value">{{ stats.pendingApproval }}</text>
        <text class="stat-label">待审批</text>
      </view>
      <view class="stat-item" @tap="navigateTo('/pages/attendance/attendance')">
        <text class="stat-value">{{ stats.workDays }}</text>
        <text class="stat-label">工作天数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.overtimeHours }}</text>
        <text class="stat-label">加班时长</text>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @tap="navigateTo('/pages/my/info')">
        <text class="menu-icon">👤</text>
        <text class="menu-label">个人信息</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/approval/approval?tab=my')">
        <text class="menu-icon">📋</text>
        <text class="menu-label">我的申请</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/attendance/attendance')">
        <text class="menu-icon">✅</text>
        <text class="menu-label">考勤记录</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/video/meeting')">
        <text class="menu-icon">📹</text>
        <text class="menu-label">视频会议</text>
        <text class="menu-arrow">></text>
      </view>
    </view>
    
    <view class="menu-section">
      <view class="menu-item" @tap="navigateTo('/pages/my/settings')">
        <text class="menu-icon">⚙️</text>
        <text class="menu-label">设置</text>
        <text class="menu-arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/my/about')">
        <text class="menu-icon">ℹ️</text>
        <text class="menu-label">关于我们</text>
        <text class="menu-arrow">></text>
      </view>
    </view>
    
    <view class="logout-section">
      <button class="btn-logout" @tap="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      userInfo: {},
      stats: {
        pendingApproval: 0,
        workDays: 0,
        overtimeHours: 0
      }
    }
  },
  computed: {
    userInitial() {
      return (this.userInfo.realName || this.userInfo.username || 'U').charAt(0).toUpperCase()
    }
  },
  onLoad() {
    this.userInfo = uni.getStorageSync('userInfo') || {}
    this.loadStats()
  },
  onShow() {
    this.loadStats()
  },
  methods: {
    async loadStats() {
      try {
        const userId = this.userInfo.id || 1
        
        const approvalRes = await api.approval.list({ 
          status: 'PENDING', 
          my: true,
          pageNum: 1, 
          pageSize: 1 
        })
        this.stats.pendingApproval = approvalRes.data.total || 0
        
        const now = new Date()
        const attendanceRes = await api.attendance.list({ 
          userId,
          startDate: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`,
          endDate: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-31`
        })
        const records = attendanceRes.data.records || []
        this.stats.workDays = records.length
        this.stats.overtimeHours = records.reduce((sum, r) => sum + (r.overtimeHours || 0), 0)
      } catch (e) {
        console.error(e)
      }
    },
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    handleLogout() {
      uni.showModal({
        title: '退出登录',
        content: '确定要退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({ url: '/pages/login/login' })
          }
        }
      })
    }
  }
}
</script>

<style>
.profile-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 30rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 64rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 20rpx;
}

.username {
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
  margin-bottom: 10rpx;
}

.department {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.stats-card {
  display: flex;
  background: #fff;
  border-radius: 20rpx;
  margin: -30rpx 30rpx 30rpx;
  padding: 40rpx 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.stat-item {
  flex: 1;
  text-align: center;
  border-right: 1rpx solid #f0f0f0;
}

.stat-item:last-child {
  border-right: none;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #667eea;
  display: block;
  margin-bottom: 10rpx;
}

.stat-label {
  font-size: 24rpx;
  color: #999;
}

.menu-section {
  background: #fff;
  margin: 20rpx 30rpx;
  border-radius: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #f5f5f5;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.menu-label {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.menu-arrow {
  color: #999;
  font-size: 28rpx;
}

.logout-section {
  padding: 40rpx 30rpx;
}

.btn-logout {
  width: 100%;
  height: 90rpx;
  line-height: 90rpx;
  background: #fff;
  color: #ff4d4f;
  border-radius: 20rpx;
  font-size: 32rpx;
}
</style>
