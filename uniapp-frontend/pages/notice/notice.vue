<template>
  <view class="notice-container">
    <view class="search-bar">
      <input 
        class="search-input" 
        v-model="searchKeyword" 
        placeholder="搜索公告标题" 
        @confirm="handleSearch"
      />
      <text class="search-btn" @tap="handleSearch">搜索</text>
    </view>
    
    <view class="notice-list">
      <view 
        class="notice-card" 
        v-for="item in noticeList" 
        :key="item.id"
        @tap="viewDetail(item.id)"
      >
        <view class="card-header">
          <view class="header-left">
            <text class="title">{{ item.title }}</text>
            <text class="tag" :class="getTagClass(item.type)">{{ getTypeName(item.type) }}</text>
          </view>
          <text class="priority" v-if="item.priority === 'HIGH'">重要</text>
        </view>
        
        <view class="card-content">
          <text class="summary">{{ item.summary || item.content }}</text>
        </view>
        
        <view class="card-footer">
          <view class="footer-left">
            <text class="publisher">👤 {{ item.publisherName }}</text>
            <text class="publish-time">{{ formatTime(item.publishTime) }}</text>
          </view>
          <view class="footer-right">
            <text class="views">👁 {{ item.viewCount || 0 }}</text>
          </view>
        </view>
      </view>
      
      <view v-if="noticeList.length === 0" class="empty">
        <text class="empty-icon">📢</text>
        <text class="empty-text">暂无公告</text>
      </view>
    </view>
    
    <view class="load-more" v-if="hasMore" @tap="loadMore">
      <text class="load-more-text">{{ loading ? '加载中...' : '加载更多' }}</text>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      searchKeyword: '',
      noticeList: [],
      pageInfo: {
        pageNum: 1,
        pageSize: 10,
        total: 0
      },
      loading: false,
      hasMore: true
    }
  },
  onLoad() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.pageInfo.pageNum = 1
    this.hasMore = true
    this.loadData()
    setTimeout(() => {
      uni.stopPullDownRefresh()
    }, 1000)
  },
  onReachBottom() {
    if (this.hasMore && !this.loading) {
      this.loadMore()
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
          keyword: this.searchKeyword
        }
        
        const res = await api.notice.list(params)
        
        if (this.pageInfo.pageNum === 1) {
          this.noticeList = res.data.records || []
        } else {
          this.noticeList = [...this.noticeList, ...(res.data.records || [])]
        }
        
        this.pageInfo.total = res.data.total || 0
        this.hasMore = this.pageInfo.pageNum * this.pageInfo.pageSize < this.pageInfo.total
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.pageInfo.pageNum = 1
      this.hasMore = true
      this.loadData()
    },
    loadMore() {
      if (this.loading || !this.hasMore) return
      this.pageInfo.pageNum++
      this.loadData()
    },
    viewDetail(id) {
      uni.navigateTo({ url: `/pages/notice/detail?id=${id}` })
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
      const date = new Date(time)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
      
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }
  }
}
</script>

<style>
.notice-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.search-bar {
  background: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  align-items: center;
  gap: 20rpx;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search-input {
  flex: 1;
  height: 70rpx;
  background: #f5f5f5;
  border-radius: 35rpx;
  padding: 0 30rpx;
  font-size: 28rpx;
}

.search-btn {
  color: #667eea;
  font-size: 28rpx;
  padding: 10rpx 20rpx;
}

.notice-list {
  padding: 20rpx;
}

.notice-card {
  background: #fff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.header-left {
  flex: 1;
  margin-right: 20rpx;
}

.title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 10rpx;
}

.tag {
  display: inline-block;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
  font-size: 22rpx;
  color: #fff;
  margin-right: 10rpx;
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
  white-space: nowrap;
}

.card-content {
  margin-bottom: 20rpx;
}

.summary {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.footer-left {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.publisher {
  font-size: 24rpx;
  color: #999;
}

.publish-time {
  font-size: 24rpx;
  color: #ccc;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.views {
  font-size: 24rpx;
  color: #999;
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

.load-more {
  text-align: center;
  padding: 30rpx;
  color: #999;
  font-size: 28rpx;
}
</style>
