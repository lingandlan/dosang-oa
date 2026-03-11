<template>
  <view class="attendance-container">
    <view class="header">
      <text class="greeting">你好，{{ userInfo.realName || userInfo.username }}</text>
      <text class="date">{{ currentDate }}</text>
      <text class="time">{{ currentTime }}</text>
    </view>
    
    <view class="clock-card">
      <view class="clock-section">
        <view class="clock-item">
          <text class="clock-label">上班打卡</text>
          <text class="clock-time">{{ workCheckin || '--:--' }}</text>
          <text class="clock-status" :class="getWorkStatusClass()">{{ getWorkStatus() }}</text>
        </view>
        <view class="clock-divider"></view>
        <view class="clock-item">
          <text class="clock-label">下班打卡</text>
          <text class="clock-time">{{ workCheckout || '--:--' }}</text>
          <text class="clock-status" :class="getOffStatusClass()">{{ getOffStatus() }}</text>
        </view>
      </view>
      
      <view class="checkin-btn-container">
        <button 
          class="checkin-btn" 
          :class="{ 'disabled': !canCheckin }"
          @tap="handleCheckin"
        >
          <text class="btn-text">{{ getButtonText() }}</text>
        </button>
        <text class="location">{{ currentLocation }}</text>
      </view>
    </view>
    
    <view class="stats-card">
      <view class="stat-item">
        <text class="stat-value">{{ stats.workDays }}</text>
        <text class="stat-label">工作天数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.lateDays }}</text>
        <text class="stat-label">迟到天数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.leaveEarlyDays }}</text>
        <text class="stat-label">早退天数</text>
      </view>
      <view class="stat-item">
        <text class="stat-value">{{ stats.overtimeHours }}</text>
        <text class="stat-label">加班时长</text>
      </view>
    </view>
    
    <view class="history-section">
      <view class="section-header">
        <text class="section-title">打卡记录</text>
        <text class="month-selector" @tap="showMonthPicker">
          {{ currentMonth }} ▼
        </text>
      </view>
      
      <view class="calendar">
        <view class="calendar-header">
          <text 
            v-for="(day, index) in weekDays" 
            :key="index"
            class="calendar-day-header"
          >
            {{ day }}
          </text>
        </view>
        <view class="calendar-body">
          <view 
            v-for="(item, index) in calendarData" 
            :key="index"
            class="calendar-day"
            :class="{ 'today': item.isToday, 'has-record': item.hasRecord }"
            @tap="showDetail(item)"
          >
            <text class="day-number">{{ item.day }}</text>
            <view class="day-dots" v-if="item.hasRecord">
              <view class="dot" :class="{ 'has-checkin': item.checkin }"></view>
              <view class="dot" :class="{ 'has-checkout': item.checkout }"></view>
            </view>
          </view>
        </view>
      </view>
    </view>
    
    <picker 
      v-if="showPicker" 
      mode="date" 
      fields="month"
      :value="currentMonth"
      @change="onMonthChange"
    >
    </picker>
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
      currentMonth: '',
      currentLocation: '获取位置中...',
      workCheckin: '',
      workCheckout: '',
      todayRecord: null,
      canCheckin: true,
      stats: {
        workDays: 0,
        lateDays: 0,
        leaveEarlyDays: 0,
        overtimeHours: 0
      },
      weekDays: ['日', '一', '二', '三', '四', '五', '六'],
      calendarData: [],
      showPicker: false,
      timer: null
    }
  },
  onLoad() {
    this.userInfo = uni.getStorageSync('userInfo') || {}
    this.updateDateTime()
    this.getLocation()
    this.loadTodayRecord()
    this.loadStats()
    this.loadCalendar()
    
    this.timer = setInterval(() => {
      this.updateDateTime()
    }, 1000)
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  onPullDownRefresh() {
    this.loadTodayRecord()
    this.loadStats()
    this.loadCalendar()
    setTimeout(() => {
      uni.stopPullDownRefresh()
    }, 1000)
  },
  methods: {
    updateDateTime() {
      const now = new Date()
      this.currentDate = now.toLocaleDateString('zh-CN', { 
        year: 'numeric', 
        month: 'long', 
        day: 'numeric',
        weekday: 'long'
      })
      this.currentTime = now.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit',
        second: '2-digit'
      })
      this.currentMonth = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    },
    getLocation() {
      uni.getLocation({
        type: 'wgs84',
        success: (res) => {
          this.currentLocation = `${res.latitude.toFixed(4)}, ${res.longitude.toFixed(4)}`
        },
        fail: () => {
          this.currentLocation = '公司'
        }
      })
    },
    async loadTodayRecord() {
      try {
        const userId = this.userInfo.id || 1
        const res = await api.attendance.today(userId)
        this.todayRecord = res.data
        if (res.data) {
          this.workCheckin = res.data.checkinTime ? res.data.checkinTime.substring(11, 16) : ''
          this.workCheckout = res.data.checkoutTime ? res.data.checkoutTime.substring(11, 16) : ''
          this.canCheckin = !res.data.checkinTime || !res.data.checkoutTime
        }
      } catch (e) {
        console.error(e)
      }
    },
    async loadStats() {
      try {
        const userId = this.userInfo.id || 1
        const res = await api.attendance.list({ 
          userId,
          startDate: `${this.currentMonth}-01`,
          endDate: `${this.currentMonth}-31`
        })
        const records = res.data.records || []
        this.stats.workDays = records.length
        this.stats.lateDays = records.filter(r => r.isLate).length
        this.stats.leaveEarlyDays = records.filter(r => r.isLeaveEarly).length
        this.stats.overtimeHours = records.reduce((sum, r) => sum + (r.overtimeHours || 0), 0)
      } catch (e) {
        console.error(e)
      }
    },
    async loadCalendar() {
      try {
        const userId = this.userInfo.id || 1
        const res = await api.attendance.list({ 
          userId,
          startDate: `${this.currentMonth}-01`,
          endDate: `${this.currentMonth}-31`
        })
        
        const records = res.data.records || []
        const recordMap = {}
        records.forEach(r => {
          const date = new Date(r.checkinTime || r.checkoutTime).getDate()
          recordMap[date] = {
            hasRecord: true,
            checkin: !!r.checkinTime,
            checkout: !!r.checkoutTime,
            data: r
          }
        })
        
        const now = new Date()
        const year = now.getFullYear()
        const month = now.getMonth()
        const firstDay = new Date(year, month, 1)
        const lastDay = new Date(year, month + 1, 0)
        const startWeekDay = firstDay.getDay()
        const totalDays = lastDay.getDate()
        const today = now.getDate()
        
        const data = []
        for (let i = 0; i < startWeekDay; i++) {
          data.push({ day: '', hasRecord: false })
        }
        
        for (let i = 1; i <= totalDays; i++) {
          const record = recordMap[i] || { hasRecord: false, checkin: false, checkout: false }
          data.push({
            day: i,
            hasRecord: record.hasRecord,
            checkin: record.checkin,
            checkout: record.checkout,
            isToday: i === today,
            data: record.data
          })
        }
        
        this.calendarData = data
      } catch (e) {
        console.error(e)
      }
    },
    async handleCheckin() {
      if (!this.canCheckin) {
        uni.showToast({ title: '今日已打卡', icon: 'none' })
        return
      }
      
      try {
        const userId = this.userInfo.id || 1
        const now = new Date()
        const hour = now.getHours()
        
        const checkType = (!this.workCheckin || hour < 13) ? 'WORK_CHECKIN' : 'WORK_CHECKOUT'
        
        await api.attendance.checkin({ 
          userId, 
          checkType,
          location: this.currentLocation
        })
        
        uni.showToast({ title: '打卡成功', icon: 'success' })
        this.loadTodayRecord()
        this.loadStats()
        this.loadCalendar()
      } catch (e) {
        console.error(e)
      }
    },
    getButtonText() {
      if (!this.workCheckin) return '上班打卡'
      if (!this.workCheckout) return '下班打卡'
      return '今日已完成'
    },
    getWorkStatus() {
      if (!this.workCheckin) return '未打卡'
      if (this.todayRecord?.isLate) return '迟到'
      return '正常'
    },
    getWorkStatusClass() {
      if (!this.workCheckin) return ''
      if (this.todayRecord?.isLate) return 'status-late'
      return 'status-normal'
    },
    getOffStatus() {
      if (!this.workCheckout) return '未打卡'
      if (this.todayRecord?.isLeaveEarly) return '早退'
      return '正常'
    },
    getOffStatusClass() {
      if (!this.workCheckout) return ''
      if (this.todayRecord?.isLeaveEarly) return 'status-late'
      return 'status-normal'
    },
    showMonthPicker() {
      this.showPicker = true
    },
    onMonthChange(e) {
      this.currentMonth = e.detail.value
      this.showPicker = false
      this.loadStats()
      this.loadCalendar()
    },
    showDetail(item) {
      if (!item.hasRecord || !item.data) return
      uni.showModal({
        title: `${this.currentMonth}-${String(item.day).padStart(2, '0')} 打卡详情`,
        content: `上班: ${item.data.checkinTime || '--'}\n下班: ${item.data.checkoutTime || '--'}\n加班: ${item.data.overtimeHours || 0}小时`,
        showCancel: false
      })
    }
  }
}
</script>

<style>
.attendance-container {
  min-height: 100vh;
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  padding-bottom: 40rpx;
}

.header {
  padding: 40rpx 30rpx;
  color: #fff;
}

.greeting {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.date {
  font-size: 28rpx;
  opacity: 0.9;
  display: block;
  margin-bottom: 5rpx;
}

.time {
  font-size: 48rpx;
  font-weight: bold;
  display: block;
}

.clock-card {
  background: #fff;
  border-radius: 30rpx;
  margin: 30rpx;
  padding: 40rpx;
}

.clock-section {
  display: flex;
  justify-content: space-around;
  margin-bottom: 40rpx;
}

.clock-item {
  text-align: center;
  flex: 1;
}

.clock-label {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-bottom: 10rpx;
}

.clock-time {
  font-size: 48rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.clock-status {
  font-size: 24rpx;
  color: #52c41a;
  background: #f6ffed;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.status-late {
  color: #ff4d4f;
  background: #fff1f0;
}

.status-normal {
  color: #52c41a;
  background: #f6ffed;
}

.clock-divider {
  width: 1rpx;
  background: #f0f0f0;
}

.checkin-btn-container {
  text-align: center;
}

.checkin-btn {
  width: 300rpx;
  height: 300rpx;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  font-size: 36rpx;
  font-weight: bold;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8rpx 20rpx rgba(102, 126, 234, 0.4);
  margin-bottom: 20rpx;
}

.checkin-btn.disabled {
  background: #d9d9d9;
  box-shadow: none;
}

.location {
  font-size: 24rpx;
  color: #999;
}

.stats-card {
  background: #fff;
  border-radius: 20rpx;
  margin: 0 30rpx 30rpx;
  padding: 30rpx;
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
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

.history-section {
  background: #fff;
  border-radius: 20rpx 20rpx 0 0;
  margin: 30rpx;
  margin-top: 0;
  padding: 30rpx;
  flex: 1;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
}

.month-selector {
  font-size: 28rpx;
  color: #667eea;
  padding: 10rpx 20rpx;
  background: #f0f5ff;
  border-radius: 10rpx;
}

.calendar-header {
  display: flex;
  margin-bottom: 20rpx;
}

.calendar-day-header {
  flex: 1;
  text-align: center;
  font-size: 24rpx;
  color: #999;
  padding: 10rpx 0;
}

.calendar-body {
  display: flex;
  flex-wrap: wrap;
}

.calendar-day {
  width: 14.28%;
  text-align: center;
  padding: 15rpx 0;
  position: relative;
}

.calendar-day.today {
  background: #f0f5ff;
  border-radius: 10rpx;
}

.calendar-day.has-record {
  background: #f6ffed;
  border-radius: 10rpx;
}

.day-number {
  font-size: 28rpx;
  color: #333;
}

.day-dots {
  display: flex;
  justify-content: center;
  gap: 4rpx;
  margin-top: 8rpx;
}

.dot {
  width: 8rpx;
  height: 8rpx;
  border-radius: 50%;
  background: #d9d9d9;
}

.dot.has-checkin {
  background: #52c41a;
}

.dot.has-checkout {
  background: #667eea;
}
</style>
