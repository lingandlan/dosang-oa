<template>
  <view class="meeting-container">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'schedule' }"
        @tap="switchTab('schedule')"
      >
        预约会议
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'history' }"
        @tap="switchTab('history')"
      >
        会议记录
      </view>
    </view>
    
    <view class="list">
      <view 
        class="card" 
        v-for="item in meetingList" 
        :key="item.id"
        @tap="viewDetail(item)"
      >
        <view class="card-header">
          <text class="meeting-title">{{ item.title }}</text>
          <text class="status" :class="getStatusClass(item.status)">{{ getStatusName(item.status) }}</text>
        </view>
        <view class="card-content">
          <view class="info-row">
            <text class="icon">🕐</text>
            <text class="value">{{ formatTime(item.startTime) }}</text>
          </view>
          <view class="info-row">
            <text class="icon">⏱</text>
            <text class="value">{{ item.duration }}分钟</text>
          </view>
          <view class="info-row">
            <text class="icon">👥</text>
            <text class="value">{{ item.participantCount || 1 }}人参与</text>
          </view>
          <view class="info-row" v-if="item.hostName">
            <text class="icon">👤</text>
            <text class="value">主持人: {{ item.hostName }}</text>
          </view>
        </view>
        <view class="card-footer">
          <button 
            v-if="item.status === 'SCHEDULED'" 
            class="btn btn-join" 
            @tap.stop="joinMeeting(item)"
          >
            加入会议
          </button>
          <button 
            v-if="item.status === 'LIVE'" 
            class="btn btn-live" 
            @tap.stop="joinMeeting(item)"
          >
            进入会议
          </button>
        </view>
      </view>
      
      <view v-if="meetingList.length === 0" class="empty">
        <text class="empty-icon">📹</text>
        <text class="empty-text">暂无会议</text>
      </view>
    </view>
    
    <view class="fab" @tap="createMeeting">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      currentTab: 'schedule',
      meetingList: [],
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      loading: false
    }
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.pageInfo.pageNum = 1
    this.loadData()
    setTimeout(() => {
      uni.stopPullDownRefresh()
    }, 1000)
  },
  onReachBottom() {
    if (this.pageInfo.pageNum * this.pageInfo.pageSize < this.pageInfo.total) {
      this.pageInfo.pageNum++
      this.loadData()
    }
  },
  methods: {
    async loadData() {
      if (this.loading) return
      this.loading = true
      
      try {
        const params = {
          pageNum: this.pageInfo.pageNum,
          pageSize: this.pageInfo.pageSize,
          type: this.currentTab
        }
        
        // 使用模拟数据
        this.meetingList = this.getMockData()
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    getMockData() {
      if (this.currentTab === 'schedule') {
        return [
          {
            id: 1,
            title: '周例会',
            startTime: new Date(Date.now() + 3600000).toISOString(),
            duration: 60,
            participantCount: 5,
            hostName: '张三',
            status: 'SCHEDULED'
          },
          {
            id: 2,
            title: '项目评审会',
            startTime: new Date(Date.now() + 86400000).toISOString(),
            duration: 90,
            participantCount: 8,
            hostName: '李四',
            status: 'SCHEDULED'
          },
          {
            id: 3,
            title: '技术分享会',
            startTime: new Date(Date.now() + 172800000).toISOString(),
            duration: 45,
            participantCount: 10,
            hostName: '王五',
            status: 'SCHEDULED'
          }
        ]
      } else {
        return [
          {
            id: 4,
            title: '部门例会',
            startTime: new Date(Date.now() - 86400000).toISOString(),
            duration: 45,
            participantCount: 6,
            hostName: '张三',
            status: 'ENDED'
          },
          {
            id: 5,
            title: '一对一沟通',
            startTime: new Date(Date.now() - 172800000).toISOString(),
            duration: 30,
            participantCount: 2,
            hostName: '李四',
            status: 'ENDED'
          }
        ]
      }
    },
    switchTab(tab) {
      this.currentTab = tab
      this.pageInfo.pageNum = 1
      this.loadData()
    },
    viewDetail(item) {
      uni.showModal({
        title: item.title,
        content: `时间: ${this.formatTime(item.startTime)}\n时长: ${item.duration}分钟\n主持人: ${item.hostName}`,
        showCancel: false
      })
    },
    joinMeeting(item) {
      uni.navigateTo({
        url: `/pages/video/call?roomId=${item.id}&title=${encodeURIComponent(item.title)}`
      })
    },
    createMeeting() {
      uni.navigateTo({
        url: '/pages/video/create'
      })
    },
    getStatusName(status) {
      const map = {
        SCHEDULED: '已预约',
        LIVE: '进行中',
        ENDED: '已结束',
        CANCELLED: '已取消'
      }
      return map[status] || status
    },
    getStatusClass(status) {
      const map = {
        SCHEDULED: 'status-scheduled',
        LIVE: 'status-live',
        ENDED: 'status-ended',
        CANCELLED: 'status-cancelled'
      }
      return map[status] || ''
    },
    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script>

<style>
.meeting-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.tabs {
  display: flex;
  background: #fff;
  padding: 20rpx 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.tab-item {
  flex: 1;
  text-align: center;
  font-size: 28rpx;
  color: #666;
  position: relative;
  padding: 10rpx 0;
}

.tab-item.active {
  color: #667eea;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60rpx;
  height: 4rpx;
  background: #667eea;
  border-radius: 2rpx;
}

.list {
  padding: 20rpx;
}

.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.meeting-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.status {
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
}

.status-scheduled {
  color: #1890ff;
  background: #e6f7ff;
}

.status-live {
  color: #52c41a;
  background: #f6ffed;
}

.status-ended {
  color: #999;
  background: #f5f5f5;
}

.status-cancelled {
  color: #ff4d4f;
  background: #fff1f0;
}

.card-content {
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  align-items: center;
  margin-bottom: 12rpx;
  font-size: 28rpx;
}

.info-row .icon {
  margin-right: 10rpx;
}

.info-row .value {
  color: #666;
}

.card-footer {
  display: flex;
  gap: 20rpx;
  margin-top: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.btn {
  flex: 1;
  height: 70rpx;
  line-height: 70rpx;
  border-radius: 35rpx;
  font-size: 28rpx;
  padding: 0;
}

.btn-join {
  background: #fff;
  border: 1rpx solid #667eea;
  color: #667eea;
}

.btn-live {
  background: #52c41a;
  color: #fff;
}

.empty {
  text-align: center;
  padding: 100rpx 0;
}

.empty-icon {
  font-size: 100rpx;
  display: block;
  margin-bottom: 20rpx;
}

.empty-text {
  font-size: 28rpx;
  color: #999;
}

.fab {
  position: fixed;
  right: 40rpx;
  bottom: 100rpx;
  width: 100rpx;
  height: 100rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(102, 126, 234, 0.4);
}

.fab-icon {
  color: #fff;
  font-size: 60rpx;
  font-weight: bold;
}
</style>
