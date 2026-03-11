<template>
  <view class="user-container">
    <view class="user-header">
      <view class="avatar">
        <text class="avatar-text">{{ userInfo.realName ? userInfo.realName.charAt(0) : userInfo.username.charAt(0) }}</text>
      </view>
      <view class="user-info">
        <text class="name">{{ userInfo.realName || userInfo.username }}</text>
        <text class="department">{{ userInfo.department || '未设置部门' }}</text>
      </view>
    </view>

    <view class="menu-list">
      <view class="menu-item" @tap="navigateTo('/pages/user/profile')">
        <view class="menu-content">
          <text class="menu-icon">👤</text>
          <text class="menu-label">个人资料</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/user/password')">
        <view class="menu-content">
          <text class="menu-icon">🔒</text>
          <text class="menu-label">修改密码</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/approval/my')">
        <view class="menu-content">
          <text class="menu-icon">📋</text>
          <text class="menu-label">我的审批</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/attendance/my')">
        <view class="menu-content">
          <text class="menu-icon">✅</text>
          <text class="menu-label">考勤记录</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="navigateTo('/pages/notice/my')">
        <view class="menu-content">
          <text class="menu-icon">📢</text>
          <text class="menu-label">我的公告</text>
        </view>
        <text class="arrow">></text>
      </view>
      <view class="menu-item" @tap="showAbout">
        <view class="menu-content">
          <text class="menu-icon">ℹ️</text>
          <text class="menu-label">关于我们</text>
        </view>
        <text class="arrow">></text>
      </view>
    </view>

    <view class="logout-section">
      <button class="btn-logout" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      userInfo: {}
    }
  },
  onLoad() {
    this.loadUserInfo()
  },
  onShow() {
    this.loadUserInfo()
  },
  methods: {
    async loadUserInfo() {
      try {
        const userInfo = uni.getStorageSync('userInfo') || {}
        this.userInfo = userInfo
      } catch (e) {
        console.error(e)
      }
    },
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定要退出登录吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.auth.logout()
            } catch (e) {
              console.error(e)
            }
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({ url: '/pages/login/login' })
          }
        }
      })
    },
    showAbout() {
      uni.showModal({
        title: '关于 OpenOA',
        content: 'OpenOA 企业办公平台\n版本：1.0.0',
        showCancel: false
      })
    }
  }
}
</script>

<style>
.user-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.user-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60rpx 40rpx;
  display: flex;
  align-items: center;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  background: rgba(255, 255, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 30rpx;
}

.avatar-text {
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.user-info {
  flex: 1;
}

.name {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.department {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.menu-list {
  margin: 30rpx 20rpx;
  background: #fff;
  border-radius: 20rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 35rpx 30rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-content {
  display: flex;
  align-items: center;
}

.menu-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.menu-label {
  font-size: 30rpx;
  color: #333;
}

.arrow {
  font-size: 28rpx;
  color: #999;
}

.logout-section {
  padding: 40rpx 20rpx;
}

.btn-logout {
  background: #ff4d4f;
  color: #fff;
  border-radius: 50rpx;
  font-size: 32rpx;
}
</style>
