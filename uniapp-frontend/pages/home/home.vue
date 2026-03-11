<template>
  <view class="home-container">
    <!-- 头部 -->
    <view class="header">
      <view class="user-info">
        <text class="greeting">你好，{{ userInfo.realName || userInfo.username }}</text>
        <text class="date">{{ currentDate }}</text>
      </view>
    </view>
    
    <!-- 功能卡片 -->
    <view class="grid">
      <view class="grid-item" @tap="navigateTo('/pages/approval/approval')">
        <text class="icon">📋</text>
        <text class="label">审批</text>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/attendance/attendance')">
        <text class="icon">✅</text>
        <text class="label">考勤</text>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/notice/notice')">
        <text class="icon">📢</text>
        <text class="label">公告</text>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/video/video')">
        <text class="icon">📹</text>
        <text class="label">视频会议</text>
      </view>
    </view>
    
    <!-- 快捷打卡 -->
    <view class="card checkin-card" v-if="!todayChecked">
      <view class="card-header">
        <text class="card-title">今日打卡</text>
        <text class="checkin-time">{{ currentTime }}</text>
      </view>
      <button class="btn-checkin" @click="handleCheckin">立即打卡</button>
    </view>
    <view class="card checked-card" v-else>
      <view class="card-header">
        <text class="card-title">已打卡</text>
        <text class="checkin-time">{{ checkinTime }}</text>
      </view>
      <text class="checked-text">今日已完成打卡</text>
    </view>
    
    <!-- 最新公告 -->
    <view class="card">
      <view class="card-header">
        <text class="card-title">最新公告</text>
        <text class="more" @tap="navigateTo('/pages/notice/notice')">更多 ></text>
      </view>
      <view class="notice-list">
        <view class="notice-item" v-for="item in notices" :key="item.id" @tap="viewNotice(item.id)">
          <text class="notice-title">{{ item.title }}</text>
          <text class="notice-time">{{ item.publishTime }}</text>
        </view>
        <view v-if="notices.length === 0" class="empty">暂无公告</view>
      </view>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      userInfo: {},
      currentDate: '',
      currentTime: '',
      todayChecked: false,
      checkinTime: '',
      notices: []
    }
  },
  onLoad() {
    const userInfo = uni.getStorageSync('userInfo') || {}
    this.userInfo = userInfo
    this.updateTime()
    this.loadNotices()
    this.timer = setInterval(() => {
      this.updateTime()
    }, 60000)
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    updateTime() {
      const now = new Date()
      this.currentDate = now.toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric' 
      })
      this.currentTime = now.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    },
    async loadNotices() {
      try {
        const res = await api.notice.list({ pageNum: 1, pageSize: 3 })
        this.notices = res.data.records || []
      } catch (e) {
        console.error(e)
        this.notices = []
      }
    },
    async checkTodayAttendance() {
      try {
        const userId = this.userInfo.id || 1
        const res = await api.attendance.today(userId)
        if (res.data && res.data.checkinTime) {
          this.checkinTime = res.data.checkinTime
          return true
        }
        return false
      } catch (e) {
        return false
      }
    },
    async handleCheckin() {
      try {
        const userId = this.userInfo.id || 1
        const res = await api.attendance.checkin({ 
          userId, 
          checkType: 'WORK_CHECKIN',
          location: '公司'
        })
        uni.showToast({ title: '打卡成功', icon: 'success' })
        this.todayChecked = true
        this.checkinTime = this.currentTime
      } catch (e) {
        console.error(e)
      }
    },
    viewNotice(id) {
      uni.navigateTo({ url: `/pages/notice/detail?id=${id}` })
    }
  }
}
</script>

<style>
.home-container {
  min-height: calc(100vh - 100rpx);
  background: #f5f5f5;
  padding: 20rpx;
  padding-bottom: 40rpx;
}

.header {
  padding: 30rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 20rpx;
  margin-bottom: 30rpx;
  box-shadow: 0 4rpx 20rpx rgba(102, 126, 234, 0.3);
}

.user-info {
  display: flex;
  flex-direction: column;
}

.greeting {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}

.date {
  font-size: 26rpx;
  color: rgba(255,255,255,0.8);
  margin-top: 10rpx;
}

.grid {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 30rpx;
}

.grid-item {
  width: 25%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx 0;
}

.icon {
  font-size: 50rpx;
  margin-bottom: 10rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
}

.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 30rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: bold;
}

.more {
  font-size: 26rpx;
  color: #999;
}

.checkin-time {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
}

.btn-checkin {
  background: #667eea;
  color: #fff;
  border-radius: 50rpx;
  margin-top: 20rpx;
}

.checked-card {
  background: linear-gradient(135deg, #52c41a 0%, #73d13d 100%);
}

.checked-text {
  font-size: 28rpx;
  color: #fff;
  text-align: center;
  display: block;
  padding: 20rpx 0;
}

.notice-list {
  max-height: 300rpx;
  overflow: hidden;
}

.notice-item {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-title {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.notice-time {
  font-size: 24rpx;
  color: #999;
}

.empty {
  text-align: center;
  color: #999;
  padding: 40rpx;
}
</style>
