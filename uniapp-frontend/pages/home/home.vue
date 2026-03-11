<template>
  <view class="home-container">
    <view class="header">
      <view class="user-info">
        <view class="avatar">
          <text>{{ (userInfo.realName || userInfo.username || 'U').charAt(0).toUpperCase() }}</text>
        </view>
        <view class="info">
          <text class="greeting">你好，{{ userInfo.realName || userInfo.username }}</text>
          <text class="date">{{ currentDate }}</text>
        </view>
        <view class="header-actions" @tap="showMenu">
          <text class="menu-icon">☰</text>
        </view>
      </view>
      <view class="quick-stats">
        <view class="stat-item">
          <text class="stat-value">{{ stats.pendingApproval }}</text>
          <text class="stat-label">待审批</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.unreadNotice }}</text>
          <text class="stat-label">未读公告</text>
        </view>
        <view class="stat-divider"></view>
        <view class="stat-item">
          <text class="stat-value">{{ stats.workDays }}</text>
          <text class="stat-label">本月天数</text>
        </view>
      </view>
    </view>
    
    <view class="grid">
      <view class="grid-item" @tap="navigateTo('/pages/approval/approval')">
        <view class="grid-icon" style="background: #ff6b6b">
          <text class="icon">📋</text>
        </view>
        <text class="label">审批</text>
        <view class="badge" v-if="stats.pendingApproval > 0">{{ stats.pendingApproval }}</view>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/attendance/attendance')">
        <view class="grid-icon" style="background: #4ecdc4">
          <text class="icon">✅</text>
        </view>
        <text class="label">考勤</text>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/notice/notice')">
        <view class="grid-icon" style="background: #ffe66d">
          <text class="icon">📢</text>
        </view>
        <text class="label">公告</text>
        <view class="badge" v-if="stats.unreadNotice > 0">{{ stats.unreadNotice }}</view>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/video/video')">
        <view class="grid-icon" style="background: #95e1d3">
          <text class="icon">📹</text>
        </view>
        <text class="label">视频会议</text>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/schedule/schedule')">
        <view class="grid-icon" style="background: #dda0dd">
          <text class="icon">📅</text>
        </view>
        <text class="label">日程</text>
      </view>
      <view class="grid-item" @tap="navigateTo('/pages/document/document')">
        <view class="grid-icon" style="background: #98d8c8">
          <text class="icon">📄</text>
        </view>
        <text class="label">文档</text>
      </view>
    </view>
    
    <view class="checkin-section" v-if="!todayChecked">
      <view class="card checkin-card">
        <view class="card-header">
          <text class="card-title">📍 考勤打卡</text>
          <text class="card-subtitle">{{ currentTime }}</text>
        </view>
        <button class="btn-checkin" @click="handleCheckin">
          <text class="btn-text">立即打卡</text>
        </button>
        <text class="location">{{ currentLocation }}</text>
      </view>
    </view>
    
    <view class="notice-section">
      <view class="section-header">
        <text class="section-title">📢 最新公告</text>
        <text class="more" @tap="navigateTo('/pages/notice/notice')">更多 ></text>
      </view>
      <view class="card">
        <view class="notice-list">
          <view class="notice-item" v-for="item in notices" :key="item.id" @tap="viewNotice(item.id)">
            <view class="notice-header">
              <text class="notice-title">{{ item.title }}</text>
              <text class="notice-tag" :class="getTagClass(item.type)">{{ getTypeName(item.type) }}</text>
            </view>
            <text class="notice-time">{{ formatTime(item.publishTime) }}</text>
          </view>
          <view v-if="notices.length === 0" class="empty">暂无公告</view>
        </view>
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
      currentLocation: '获取位置中...',
      todayChecked: false,
      notices: [],
      stats: {
        pendingApproval: 0,
        unreadNotice: 0,
        workDays: 0
      },
      timer: null
    }
  },
  onLoad() {
    this.userInfo = uni.getStorageSync('userInfo') || {}
    this.updateDateTime()
    this.getLocation()
    this.loadNotices()
    this.loadStats()
    this.checkTodayCheckin()
    
    this.timer = setInterval(() => {
      this.updateDateTime()
    }, 1000)
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  onShow() {
    this.loadStats()
    this.checkTodayCheckin()
  },
  onPullDownRefresh() {
    this.loadNotices()
    this.loadStats()
    this.checkTodayCheckin()
    setTimeout(() => {
      uni.stopPullDownRefresh()
    }, 1000)
  },
  methods: {
    updateDateTime() {
      const now = new Date()
      this.currentDate = now.toLocaleDateString('zh-CN', { 
        month: 'long', 
        day: 'numeric',
        weekday: 'long'
      })
      this.currentTime = now.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    },
    getLocation() {
      uni.getLocation({
        type: 'wgs84',
        success: (res) => {
          this.currentLocation = `当前位置: ${res.latitude.toFixed(4)}, ${res.longitude.toFixed(4)}`
        },
        fail: () => {
          this.currentLocation = '当前位置: 公司'
        }
      })
    },
    async loadNotices() {
      try {
        const res = await api.notice.list({ pageNum: 1, pageSize: 3 })
        this.notices = res.data.records || []
      } catch (e) {
        console.error(e)
      }
    },
    async loadStats() {
      try {
        const pendingRes = await api.approval.list({ 
          status: 'PENDING', 
          pageNum: 1, 
          pageSize: 1 
        })
        this.stats.pendingApproval = pendingRes.data.total || 0
        
        const noticeRes = await api.notice.list({ 
          unread: true, 
          pageNum: 1, 
          pageSize: 1 
        })
        this.stats.unreadNotice = noticeRes.data.total || 0
        
        const now = new Date()
        const userId = this.userInfo.id || 1
        const attendanceRes = await api.attendance.list({ 
          userId,
          startDate: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-01`,
          endDate: `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-31`
        })
        this.stats.workDays = (attendanceRes.data.records || []).length
      } catch (e) {
        console.error(e)
      }
    },
    async checkTodayCheckin() {
      try {
        const userId = this.userInfo.id || 1
        const res = await api.attendance.today(userId)
        if (res.data?.checkinTime) {
          this.todayChecked = true
        }
      } catch (e) {
        console.error(e)
      }
    },
    async handleCheckin() {
      try {
        const userId = this.userInfo.id || 1
        await api.attendance.checkin({ 
          userId, 
          checkType: 'WORK_CHECKIN',
          location: this.currentLocation
        })
        uni.showToast({ title: '打卡成功', icon: 'success' })
        this.todayChecked = true
        this.loadStats()
      } catch (e) {
        console.error(e)
      }
    },
    navigateTo(url) {
      uni.navigateTo({ url })
    },
    viewNotice(id) {
      uni.navigateTo({ url: `/pages/notice/detail?id=${id}` })
    },
    showMenu() {
      uni.showActionSheet({
        itemList: ['个人中心', '设置', '退出登录'],
        success: (res) => {
          if (res.tapIndex === 2) {
            uni.removeStorageSync('token')
            uni.removeStorageSync('userInfo')
            uni.reLaunch({ url: '/pages/login/login' })
          }
        }
      })
    },
    getTypeName(type) {
      const map = {
        COMPANY: '公司',
        DEPARTMENT: '部门',
        PERSONAL: '个人',
        URGENT: '紧急'
      }
      return map[type] || ''
    },
    getTagClass(type) {
      const map = {
        COMPANY: 'tag-company',
        DEPARTMENT: 'tag-department',
        PERSONAL: 'tag-personal',
        URGENT: 'tag-urgent'
      }
      return map[type] || ''
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      const diff = Date.now() - date.getTime()
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      
      return date.toLocaleDateString('zh-CN', {
        month: '2-digit',
        day: '2-digit'
      })
    }
  }
}
</script>

<style>
.home-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 30rpx 30rpx 40rpx;
  border-radius: 0 0 40rpx 40rpx;
  margin-bottom: 30rpx;
}

.user-info {
  display: flex;
  align-items: center;
  margin-bottom: 30rpx;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  color: #fff;
  font-weight: bold;
  margin-right: 20rpx;
}

.info {
  flex: 1;
}

.greeting {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
  display: block;
}

.date {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-top: 6rpx;
}

.header-actions {
  padding: 10rpx;
}

.menu-icon {
  font-size: 40rpx;
  color: rgba(255, 255, 255, 0.9);
}

.quick-stats {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
  padding: 30rpx 20rpx;
  display: flex;
  align-items: center;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-value {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
  display: block;
  margin-bottom: 8rpx;
}

.stat-label {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
}

.stat-divider {
  width: 1rpx;
  height: 60rpx;
  background: rgba(255, 255, 255, 0.3);
}

.grid {
  display: flex;
  flex-wrap: wrap;
  padding: 0 20rpx;
  margin-bottom: 30rpx;
}

.grid-item {
  width: 33.33%;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 30rpx 0;
  position: relative;
}

.grid-icon {
  width: 100rpx;
  height: 100rpx;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15rpx;
}

.icon {
  font-size: 48rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
}

.badge {
  position: absolute;
  top: 20rpx;
  right: 30rpx;
  background: #ff4d4f;
  color: #fff;
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
  min-width: 32rpx;
  text-align: center;
}

.checkin-section {
  padding: 0 20rpx;
  margin-bottom: 30rpx;
}

.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
}

.checkin-card {
  text-align: center;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.card-subtitle {
  font-size: 28rpx;
  color: #999;
}

.btn-checkin {
  width: 200rpx;
  height: 200rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.4);
  margin-bottom: 20rpx;
}

.location {
  font-size: 24rpx;
  color: #999;
}

.notice-section {
  padding: 0 20rpx;
  margin-bottom: 30rpx;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.more {
  font-size: 26rpx;
  color: #667eea;
}

.notice-list {
  max-height: 400rpx;
  overflow: hidden;
}

.notice-item {
  padding: 25rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10rpx;
}

.notice-title {
  font-size: 28rpx;
  color: #333;
  font-weight: 500;
  flex: 1;
  margin-right: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-tag {
  font-size: 20rpx;
  padding: 4rpx 10rpx;
  border-radius: 6rpx;
  color: #fff;
  white-space: nowrap;
}

.tag-company {
  background: #1890ff;
}

.tag-department {
  background: #52c41a;
}

.tag-personal {
  background: #faad14;
}

.tag-urgent {
  background: #ff4d4f;
}

.notice-time {
  font-size: 24rpx;
  color: #999;
}

.empty {
  text-align: center;
  color: #999;
  padding: 60rpx 0;
  font-size: 28rpx;
}
</style>
