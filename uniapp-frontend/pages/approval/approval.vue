<template>
  <view class="approval-container">
    <view class="tabs">
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'pending' }"
        @tap="switchTab('pending')"
      >
        待审批
        <text class="badge" v-if="pendingCount > 0">{{ pendingCount }}</text>
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'processed' }"
        @tap="switchTab('processed')"
      >
        已审批
      </view>
      <view 
        class="tab-item" 
        :class="{ active: currentTab === 'my' }"
        @tap="switchTab('my')"
      >
        我的申请
      </view>
    </view>
    
    <view class="list">
      <view 
        class="card" 
        v-for="item in approvalList" 
        :key="item.id"
        @tap="viewDetail(item.id)"
      >
        <view class="card-header">
          <text class="type-tag" :class="getTypeClass(item.type)">{{ getTypeName(item.type) }}</text>
          <text class="status" :class="getStatusClass(item.status)">{{ getStatusName(item.status) }}</text>
        </view>
        <view class="card-content">
          <view class="info-row">
            <text class="label">申请人：</text>
            <text class="value">{{ item.applicantName }}</text>
          </view>
          <view class="info-row">
            <text class="label">申请时间：</text>
            <text class="value">{{ item.applyTime }}</text>
          </view>
          <view class="info-row" v-if="item.type === 'LEAVE'">
            <text class="label">请假类型：</text>
            <text class="value">{{ item.leaveType }}</text>
          </view>
          <view class="info-row" v-if="item.type === 'LEAVE'">
            <text class="label">请假天数：</text>
            <text class="value">{{ item.leaveDays }}天</text>
          </view>
          <view class="info-row" v-if="item.type === 'OVERTIME'">
            <text class="label">加班时长：</text>
            <text class="value">{{ item.overtimeHours }}小时</text>
          </view>
          <view class="info-row" v-if="item.type === 'EXPENSE'">
            <text class="label">报销金额：</text>
            <text class="value">{{ item.amount }}元</text>
          </view>
        </view>
        <view class="card-footer" v-if="currentTab === 'pending'">
          <button class="btn btn-reject" @tap.stop="handleReject(item.id)">拒绝</button>
          <button class="btn btn-approve" @tap.stop="handleApprove(item.id)">同意</button>
        </view>
      </view>
      
      <view v-if="approvalList.length === 0" class="empty">
        <text class="empty-icon">📋</text>
        <text class="empty-text">暂无数据</text>
      </view>
    </view>
    
    <view class="fab" @tap="createApproval">
      <text class="fab-icon">+</text>
    </view>
  </view>
</template>

<script>
import api from '@/utils/api.js'

export default {
  data() {
    return {
      currentTab: 'pending',
      approvalList: [],
      pendingCount: 0,
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
        let params = {
          pageNum: this.pageInfo.pageNum,
          pageSize: this.pageInfo.pageSize
        }
        
        if (this.currentTab === 'pending') {
          params.status = 'PENDING'
        } else if (this.currentTab === 'processed') {
          params.status = 'APPROVED,REJECTED'
        } else {
          params.my = true
        }
        
        const res = await api.approval.list(params)
        
        if (this.pageInfo.pageNum === 1) {
          this.approvalList = res.data.records || []
        } else {
          this.approvalList = [...this.approvalList, ...(res.data.records || [])]
        }
        
        this.pageInfo.total = res.data.total || 0
        
        if (this.currentTab === 'pending') {
          this.pendingCount = this.pageInfo.total
        }
      } catch (e) {
        console.error(e)
      } finally {
        this.loading = false
      }
    },
    switchTab(tab) {
      this.currentTab = tab
      this.pageInfo.pageNum = 1
      this.loadData()
    },
    viewDetail(id) {
      uni.navigateTo({ url: `/pages/approval/detail?id=${id}` })
    },
    async handleApprove(id) {
      uni.showModal({
        title: '确认审批',
        content: '确定同意该申请吗？',
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.approval.approve(id, { action: 'APPROVE' })
              uni.showToast({ title: '审批成功', icon: 'success' })
              this.pageInfo.pageNum = 1
              this.loadData()
            } catch (e) {
              console.error(e)
            }
          }
        }
      })
    },
    async handleReject(id) {
      uni.showModal({
        title: '拒绝申请',
        content: '请输入拒绝理由',
        editable: true,
        placeholderText: '请输入拒绝理由',
        success: async (res) => {
          if (res.confirm) {
            try {
              await api.approval.approve(id, { 
                action: 'REJECT',
                reason: res.content || '已拒绝'
              })
              uni.showToast({ title: '已拒绝', icon: 'success' })
              this.pageInfo.pageNum = 1
              this.loadData()
            } catch (e) {
              console.error(e)
            }
          }
        }
      })
    },
    createApproval() {
      uni.navigateTo({ url: '/pages/approval/create' })
    },
    getTypeName(type) {
      const map = {
        LEAVE: '请假',
        OVERTIME: '加班',
        EXPENSE: '报销',
        REIMBURSEMENT: '报销'
      }
      return map[type] || type
    },
    getTypeClass(type) {
      const map = {
        LEAVE: 'type-leave',
        OVERTIME: 'type-overtime',
        EXPENSE: 'type-expense',
        REIMBURSEMENT: 'type-expense'
      }
      return map[type] || ''
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
    }
  }
}
</script>

<style>
.approval-container {
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

.badge {
  position: absolute;
  top: 0;
  right: 30rpx;
  background: #ff4d4f;
  color: #fff;
  font-size: 20rpx;
  padding: 2rpx 10rpx;
  border-radius: 10rpx;
  min-width: 32rpx;
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

.type-tag {
  padding: 6rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  color: #fff;
}

.type-leave {
  background: #52c41a;
}

.type-overtime {
  background: #faad14;
}

.type-expense {
  background: #1890ff;
}

.status {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
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

.card-content {
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  margin-bottom: 10rpx;
  font-size: 28rpx;
}

.label {
  color: #999;
  margin-right: 10rpx;
  width: 140rpx;
}

.value {
  color: #333;
  flex: 1;
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

.btn-reject {
  background: #fff;
  border: 1rpx solid #d9d9d9;
  color: #666;
}

.btn-approve {
  background: #667eea;
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
