<template>
  <view class="call-container">
    <view class="video-area">
      <view class="remote-video" v-if="!isMuted">
        <view class="video-placeholder">
          <text class="placeholder-icon">📹</text>
          <text class="placeholder-text">正在连接会议...</text>
        </view>
      </view>
      <view class="muted-overlay" v-else>
        <text class="muted-text">对方已关闭摄像头</text>
      </view>
      
      <view class="local-video" v-if="!isCameraOff">
        <view class="local-placeholder">
          <text class="avatar">{{ userInitial }}</text>
        </view>
      </view>
      <view class="camera-off" v-else>
        <text class="camera-off-text">📷</text>
      </view>
    </view>
    
    <view class="meeting-info">
      <text class="meeting-title">{{ meetingTitle }}</text>
      <text class="meeting-time">{{ currentTime }}</text>
      <text class="participant-count">👥 {{ participantCount }}人参与</text>
    </view>
    
    <view class="control-bar">
      <view class="control-btn" :class="{ 'off': isMuted }" @tap="toggleMute">
        <text class="control-icon">{{ isMuted ? '🔇' : '🎤' }}</text>
        <text class="control-label">{{ isMuted ? '取消静音' : '静音' }}</text>
      </view>
      
      <view class="control-btn" :class="{ 'off': isCameraOff }" @tap="toggleCamera">
        <text class="control-icon">{{ isCameraOff ? '📷' : '📹' }}</text>
        <text class="control-label">{{ isCameraOff ? '开启摄像头' : '关闭摄像头' }}</text>
      </view>
      
      <view class="control-btn" @tap="toggleSpeaker">
        <text class="control-icon">{{ isSpeakerOn ? '🔊' : '🔉' }}</text>
        <text class="control-label">{{ isSpeakerOn ? '扬声器' : '听筒' }}</text>
      </view>
      
      <view class="control-btn" @tap="showParticipants">
        <text class="control-icon">👥</text>
        <text class="control-label">成员</text>
      </view>
      
      <view class="control-btn end-call" @tap="endCall">
        <text class="control-icon">📞</text>
        <text class="control-label">挂断</text>
      </view>
    </view>
    
    <view class="participants-panel" v-if="showParticipantsPanel">
      <view class="panel-header">
        <text class="panel-title">参会成员 ({{ participants.length }})</text>
        <text class="close-btn" @tap="showParticipantsPanel = false">✕</text>
      </view>
      <scroll-view class="participant-list" scroll-y>
        <view 
          class="participant-item" 
          v-for="p in participants" 
          :key="p.id"
        >
          <view class="participant-avatar">
            <text>{{ p.name.charAt(0) }}</text>
          </view>
          <text class="participant-name">{{ p.name }}</text>
          <text class="participant-role" v-if="p.isHost">主持人</text>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      roomId: '',
      meetingTitle: '会议',
      userInfo: {},
      isMuted: false,
      isCameraOff: false,
      isSpeakerOn: true,
      showParticipantsPanel: false,
      currentTime: '',
      participantCount: 1,
      participants: [],
      timer: null
    }
  },
  computed: {
    userInitial() {
      return (this.userInfo.realName || this.userInfo.username || 'U').charAt(0).toUpperCase()
    }
  },
  onLoad(options) {
    this.roomId = options.roomId || '1'
    this.meetingTitle = decodeURIComponent(options.title || '会议')
    this.userInfo = uni.getStorageSync('userInfo') || {}
    
    this.initParticipants()
    this.updateTime()
    
    this.timer = setInterval(() => {
      this.updateTime()
    }, 1000)
  },
  onUnload() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  onBackPress() {
    this.showEndCallConfirm()
    return true
  },
  methods: {
    initParticipants() {
      this.participants = [
        {
          id: 1,
          name: this.userInfo.realName || this.userInfo.username || '我',
          isHost: true
        },
        {
          id: 2,
          name: '张三',
          isHost: false
        }
      ]
      this.participantCount = this.participants.length
    },
    updateTime() {
      const now = new Date()
      this.currentTime = now.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    },
    toggleMute() {
      this.isMuted = !this.isMuted
      uni.showToast({
        title: this.isMuted ? '已静音' : '已取消静音',
        icon: 'none'
      })
    },
    toggleCamera() {
      this.isCameraOff = !this.isCameraOff
      uni.showToast({
        title: this.isCameraOff ? '摄像头已关闭' : '摄像头已开启',
        icon: 'none'
      })
    },
    toggleSpeaker() {
      this.isSpeakerOn = !this.isSpeakerOn
      uni.showToast({
        title: this.isSpeakerOn ? '扬声器模式' : '听筒模式',
        icon: 'none'
      })
    },
    showParticipants() {
      this.showParticipantsPanel = true
    },
    endCall() {
      this.showEndCallConfirm()
    },
    showEndCallConfirm() {
      uni.showModal({
        title: '结束通话',
        content: '确定要结束当前会议吗？',
        success: (res) => {
          if (res.confirm) {
            uni.showToast({ title: '已离开会议', icon: 'none' })
            setTimeout(() => {
              uni.navigateBack()
            }, 1000)
          }
        }
      })
    }
  }
}
</script>

<style>
.call-container {
  min-height: 100vh;
  background: #1a1a1a;
  display: flex;
  flex-direction: column;
}

.video-area {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remote-video {
  width: 100%;
  height: 100%;
  background: #2a2a2a;
}

.video-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.placeholder-icon {
  font-size: 120rpx;
  margin-bottom: 30rpx;
}

.placeholder-text {
  color: #999;
  font-size: 28rpx;
}

.muted-overlay {
  width: 100%;
  height: 100%;
  background: #2a2a2a;
  display: flex;
  align-items: center;
  justify-content: center;
}

.muted-text {
  color: #999;
  font-size: 32rpx;
}

.local-video {
  position: absolute;
  right: 30rpx;
  top: 30rpx;
  width: 200rpx;
  height: 300rpx;
  background: #333;
  border-radius: 20rpx;
  overflow: hidden;
}

.local-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48rpx;
  color: #fff;
  font-weight: bold;
}

.camera-off {
  position: absolute;
  right: 30rpx;
  top: 30rpx;
  width: 200rpx;
  height: 300rpx;
  background: #333;
  border-radius: 20rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.camera-off-text {
  font-size: 80rpx;
}

.meeting-info {
  padding: 30rpx;
  text-align: center;
  color: #fff;
}

.meeting-title {
  font-size: 36rpx;
  font-weight: bold;
  display: block;
  margin-bottom: 10rpx;
}

.meeting-time {
  font-size: 28rpx;
  color: #999;
  display: block;
  margin-bottom: 10rpx;
}

.participant-count {
  font-size: 26rpx;
  color: #666;
}

.control-bar {
  display: flex;
  justify-content: space-around;
  padding: 30rpx 20rpx 60rpx;
  background: #1a1a1a;
}

.control-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20rpx;
}

.control-btn.off {
  opacity: 0.6;
}

.control-btn.end-call {
  background: #ff4d4f;
  border-radius: 50%;
  width: 120rpx;
  height: 120rpx;
  justify-content: center;
  margin-top: -30rpx;
}

.control-icon {
  font-size: 48rpx;
  margin-bottom: 10rpx;
}

.control-label {
  font-size: 24rpx;
  color: #fff;
}

.participants-panel {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  background: #2a2a2a;
  border-radius: 30rpx 30rpx 0 0;
  max-height: 60vh;
  z-index: 100;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  border-bottom: 1rpx solid #3a3a3a;
}

.panel-title {
  font-size: 32rpx;
  color: #fff;
  font-weight: bold;
}

.close-btn {
  font-size: 40rpx;
  color: #999;
  padding: 10rpx;
}

.participant-list {
  max-height: 50vh;
  padding: 20rpx 30rpx;
}

.participant-item {
  display: flex;
  align-items: center;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #3a3a3a;
}

.participant-avatar {
  width: 80rpx;
  height: 80rpx;
  background: #667eea;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-right: 20rpx;
}

.participant-name {
  flex: 1;
  font-size: 28rpx;
  color: #fff;
}

.participant-role {
  font-size: 24rpx;
  color: #faad14;
  background: rgba(250, 173, 20, 0.2);
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
</style>
