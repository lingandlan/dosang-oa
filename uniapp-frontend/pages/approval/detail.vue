<template>
  <view class="detail-container">
    <view class="detail-header">
      <text class="title">{{ approval.title || getTypeName(approval.type) }}</text>
      <text class="status" :class="getStatusClass(approval.status)">{{ getStatusName(approval.status) }}</text>
    </view>
    
    <view class="detail-info">
      <view class="info-row">
        <text class="label">申请人：</text>
        <text class="value">{{ approval.applicantName }}</text>
      </view>
      <view class="info-row">
        <text class="label">申请时间：</text>
        <text class="value">{{ formatTime(approval.applyTime) }}</text>
      </view>
      <view class="info-row" v-if="approval.approver">
        <text class="label">审批人：</text>
        <text class="value">{{ approval.approverName }}</text>
      </view>
      <view class="info-row" v-if="approval.approveTime">
        <text class="label">审批时间：</text>
        <text class="value">{{ formatTime(approval.approveTime) }}</text>
      </view>
    </view>
    
    <view class="detail-content">
      <view class="content-title">申请详情</view>
      
      <view v-if="approval.type === 'LEAVE'" class="content-section">
        <view class="info-row">
          <text class="label">请假类型：</text>
          <text class="value">{{ approval.leaveType }}</text>
        </view>
        <view class="info-row">
          <text class="label">请假天数：</text>
          <text class="value">{{ approval.leaveDays }}天</text>
        </view>
        <view class="info-row">
          <text class="label">开始时间：</text>
          <text class="value">{{ approval.startTime }}</text>
        </view>
        <view class="info-row">
          <text class="label">结束时间：</text>
          <text class="value">{{ approval.endTime }}</text>
        </view>
      </view>
      
      <view v-if="approval.type === 'OVERTIME'" class="content-section">
        <view class="info-row">
          <text class="label">加班日期：</text>
          <text class="value">{{ approval.overtimeDate }}</text>
        </view>
        <view class="info-row">
          <text class="label">加班时长：</text>
          <text class="value">{{ approval.overtimeHours }}小时</text>
        </view>
        <view class="info-row">
          <text class="label">开始时间：</text>
          <text class="value">{{ approval.startTime }}</text>
        </view>
        <view class="info-row">
          <text class="label">结束时间：</text>
          <text class="value">{{ approval.endTime }}</text>
        </view>
      </view>
      
      <view v-if="approval.type === 'EXPENSE'" class="content-section">
        <view class="info-row">
          <text class="label">报销类型：</text>
          <text class="value">{{ approval.expenseType }}</text>
        </view>
        <view class="info-row">
          <text class="label">报销金额：</text>
          <text class="value">{{ approval.amount }}元</text>
        </view>
        <view class="info-row">
          <text class="label">报销日期：</text>
          <text class="value">{{ approval.expenseDate }}</text>
        </view>
      </view>
      
      <view class="info-row">
        <text class="label">申请理由：</text>
      </view>
      <view class="reason">
        {{ approval.reason }}
      </view>
    </view>
    
    <view class="detail-images" v-if="images.length > 0">
      <text class="images-title">相关图片</text>
      <view class="image-list">
        <image 
          class="image-item" 
          v-for="(item, index) in images" 
          :key="index"
          :src="item.url"
          mode="aspectFill"
          @tap="previewImage(index)"
        />
      </view>
    </view>
    
    <view class="detail-flow" v-if="flowList.length > 0">
      <text class="flow-title">审批流程</text>
      <view class="flow-list">
        <view 
          class="flow-item" 
          v-for="(item, index) in flowList" 
          :key="index"
          :class="{ 'active': item.status === 'COMPLETED' }"
        >
          <view class="flow-icon">
            <text>{{ index + 1 }}</text>
          </view>
          <view class="flow-content">
            <text class="flow-name">{{ item.approverName }}</text>
            <text class="flow-time">{{ formatTime(item.time) }}</text>
            <text class="flow-status" v-if="item.comment">意见：{{ item.comment }}</text>
          </view>
        </view>
      </view>
    </view>
    
    <view class="detail-footer" v-if="approval.status === 'PENDING'">
      <button class="btn btn-reject" @tap="handleReject">拒绝</button>
      <button class="btn btn-approve" @tap="handleApprove">同意</button>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      approvalId: '',
      approval: {},
      images: [],
      flowList: []
    }
  },
  onLoad(options) {
    this.approvalId = options.id
    this.loadDetail()
  },
  methods: {
    async loadDetail() {
      try {
        const res = await api.approval.getById(this.approvalId)
        this.approval = res.data
        this.images = res.data.images || []
        this.flowList = res.data.flowList || []
      } catch (e) {
        console.error(e)
      }
    },
    async handleApprove() {
      uni.showModal({
        title: '确认审批',
        content: '确定同意该申请吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.approval.approve(this.approvalId, { action: 'APPROVE' })
              uni.showToast({ title: '审批成功', icon: 'success' })
              this.loadDetail()
            } catch (e) {
              console.error(e)
            }
          }
        }
      })
    },
    async handleReject() {
      uni.showModal({
        title: '拒绝申请',
        content: '请输入拒绝理由',
        editable: true,
        placeholderText: '请输入拒绝理由',
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.approval.approve(this.approvalId, { 
                action: 'REJECT',
                reason: res.content || '已拒绝'
              })
              uni.showToast({ title: '已拒绝', icon: 'success' })
              this.loadDetail()
            } catch (e) {
              console.error(e)
            }
          }
        }
      })
    },
    previewImage(index) {
      uni.previewImage({
        current: index,
        urls: this.images.map(img => img.url)
      })
    },
    getTypeName(type) {
      const map = {
        LEAVE: '请假申请',
        OVERTIME: '加班申请',
        EXPENSE: '报销申请',
        REIMBURSEMENT: '报销申请'
      }
      return map[type] || type
    },
    getStatusName(status) {
      const map = {
        PENDING: '待审批',
        APPROVED: '已通过',
        REJECTED: '已拒绝'
      }
      return map[status] || status
    },
    getStatusClass(status) {
      const map = {
        PENDING: 'status-pending',
        APPROVED: 'status-approved',
        REJECTED: 'status-rejected'
      }
      return map[status] || ''
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
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  flex: 1;
}

.status {
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  white-space: nowrap;
  margin-left: 20rpx;
}

.status-pending {
  color: #faad14;
  background: #fffbe6;
}

.status-approved {
  color: #52c41a;
  background: #f6ffed;
}

.status-rejected {
  color: #ff4d4f;
  background: #fff1f0;
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
}

.content-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.content-section {
  margin-bottom: 20rpx;
}

.reason {
  font-size: 28rpx;
  color: #666;
  line-height: 1.6;
  padding: 20rpx;
  background: #f5f5f5;
  border-radius: 10rpx;
  margin-top: 10rpx;
}

.detail-images {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.images-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15rpx;
}

.image-item {
  width: 200rpx;
  height: 200rpx;
  border-radius: 10rpx;
}

.detail-flow {
  background: #fff;
  padding: 30rpx;
  margin-bottom: 20rpx;
}

.flow-title {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  display: block;
  margin-bottom: 20rpx;
}

.flow-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.flow-item {
  display: flex;
  align-items: flex-start;
  gap: 20rpx;
}

.flow-icon {
  width: 50rpx;
  height: 50rpx;
  border-radius: 50%;
  background: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24rpx;
  color: #999;
  flex-shrink: 0;
}

.flow-item.active .flow-icon {
  background: #52c41a;
  color: #fff;
}

.flow-content {
  flex: 1;
  padding-top: 5rpx;
}

.flow-name {
  font-size: 28rpx;
  color: #333;
  display: block;
  margin-bottom: 5rpx;
}

.flow-time {
  font-size: 24rpx;
  color: #999;
  display: block;
  margin-bottom: 5rpx;
}

.flow-status {
  font-size: 24rpx;
  color: #666;
  display: block;
}

.detail-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 20rpx 30rpx;
  display: flex;
  gap: 20rpx;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  border-radius: 40rpx;
  font-size: 32rpx;
  padding: 0;
}

.btn-reject {
  background: #fff;
  border: 1rpx solid #d9d9d9;
  color: #666;
}

.btn-approve {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}
</style>
