<template>
  <view class="detail-container">
    <view class="detail-header">
      <text class="title">{{ notice.title }}</text>
      <view class="meta">
        <text class="tag" :class="getTagClass(notice.type)">{{ getTypeName(notice.type) }}</text>
        <text class="priority" v-if="notice.priority === 'HIGH'">重要</text>
      </view>
    </view>
    
    <view class="detail-info">
      <view class="info-row">
        <text class="label">发布人：</text>
        <text class="value">{{ notice.publisherName }}</text>
      </view>
      <view class="info-row">
        <text class="label">发布时间：</text>
        <text class="value">{{ formatTime(notice.publishTime) }}</text>
      </view>
      <view class="info-row">
        <text class="label">浏览次数：</text>
        <text class="value">{{ notice.viewCount || 0 }}</text>
      </view>
    </view>
    
    <view class="detail-content">
      <rich-text :nodes="notice.content"></rich-text>
    </view>
    
    <view class="detail-attachments" v-if="attachments.length > 0">
      <text class="attachments-title">附件</text>
      <view class="attachment-list">
        <view 
          class="attachment-item" 
          v-for="(item, index) in attachments" 
          :key="index"
          @tap="downloadAttachment(item)"
        >
          <text class="attachment-icon">📎</text>
          <text class="attachment-name">{{ item.name }}</text>
          <text class="attachment-size">{{ formatSize(item.size) }}</text>
        </view>
      </view>
    </view>
    
    <view class="detail-footer">
      <view class="stat-bar">
        <view class="stat-item" @tap="handleLike">
          <text class="stat-icon">{{ notice.isLiked ? '❤️' : '🤍' }}</text>
          <text class="stat-count">{{ notice.likeCount || 0 }}</text>
        </view>
        <view class="stat-item">
          <text class="stat-icon">👁</text>
          <text class="stat-count">{{ notice.viewCount || 0 }}</text>
        </view>
        <view class="stat-item" @tap="showComments">
          <text class="stat-icon">💬</text>
          <text class="stat-count">{{ notice.commentCount || 0 }}</text>
        </view>
      </view>
      <button class="btn-share" @tap="handleShare">分享</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      noticeId: '',
      notice: {},
      attachments: []
    }
  },
  onLoad(options) {
    this.noticeId = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      try {
        const res = await api.notice.getById(this.noticeId)
        this.notice = res.data
        this.attachments = res.data.attachments || []
      } catch (e) {
        console.error(e)
      }
    },
    async handleLike() {
      try {
        const isLiked = this.notice.isLiked
        await api.notice.like(this.noticeId)
        this.notice.isLiked = !isLiked
        this.notice.likeCount = isLiked ? (this.notice.likeCount || 0) - 1 : (this.notice.likeCount || 0) + 1
      } catch (e) {
        console.error(e)
      }
    },
    handleShare() {
      uni.share({
        provider: 'weixin',
        scene: 'WXSceneSession',
        type: 0,
        href: `/pages/notice/detail?id=${this.noticeId}`,
        title: this.notice.title,
        summary: this.notice.summary,
        success: () => {
          uni.showToast({ title: '分享成功', icon: 'success' })
        },
        fail: () => {
          uni.showToast({ title: '分享失败', icon: 'none' })
        }
      })
    },
    showComments() {
      uni.navigateTo({ url: `/pages/notice/comments?id=${this.noticeId}` })
    },
    downloadAttachment(item) {
      uni.downloadFile({
        url: item.url,
        success: (res) => {
          uni.openDocument({
            filePath: res.tempFilePath,
            success: () => {
              uni.showToast({ title: '打开成功', icon: 'success' })
            }
          })
        }
      })
    },
    getTypeName(type) {
      const map = {
        COMPANY: '公司公告',
        DEPARTMENT: '部门公告',
        PERSONAL: '个人通知',
        URGENT: '紧急通知'
      }
      return map[type] || type
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
      return new Date(time).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    },
    formatSize(size) {
      if (size < 1024) return `${size}B`
      if (size < 1024 * 1024) return `${(size / 1024).toFixed(1)}KB`
      return `${(size / 1024 / 1024).toFixed(1)}MB`
    }
  }
}
</script>

<style>
.detail-container {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 120rpx;
}

.detail-header {
  background: #fff;
  padding: 40rpx 30rpx 30rpx;
  margin-bottom: 20rpx;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.meta {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.tag {
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #fff;
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

.priority {
  background: #ff4d4f;
  color: #fff;
  font-size: 22rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.detail-info {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 15rpx;
  font-size: 28rpx;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #999;
  margin-right: 20rpx;
  width: 140rpx;
}

.value {
  color: #333;
  flex: 1;
}

.detail-content {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
  line-height: 1.8;
  font-size: 30rpx;
  color: #333;
}

.detail-attachments {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.attachments-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 15rpx;
}

.attachment-item {
  display: flex;
  align-items: center;
  padding: 20rpx;
  background: #f5f5f5;
  border-radius: 10rpx;
}

.attachment-icon {
  font-size: 36rpx;
  margin-right: 15rpx;
}

.attachment-name {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.attachment-size {
  font-size: 24rpx;
  color: #999;
}

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.stat-bar {
  flex: 1;
  display: flex;
  justify-content: space-around;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.stat-icon {
  font-size: 36rpx;
}

.stat-count {
  font-size: 28rpx;
  color: #999;
}

.btn-share {
  width: 180rpx;
  height: 70rpx;
  line-height: 70rpx;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-radius: 35rpx;
  font-size: 28rpx;
  padding: 0;
}
</style>
