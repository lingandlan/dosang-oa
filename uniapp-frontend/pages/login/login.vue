<template>
  <view class="login-container">
    <view class="logo">
      <text class="title">OpenOA</text>
      <text class="subtitle">企业办公平台</text>
    </view>
    
    <view class="form">
      <view class="input-wrapper">
        <text class="input-icon">👤</text>
        <input 
          class="input" 
          v-model="form.username" 
          placeholder="请输入用户名"
          @blur="saveFormData"
        />
      </view>
      <view class="input-wrapper">
        <text class="input-icon">🔒</text>
        <input 
          class="input" 
          v-model="form.password" 
          :type="showPassword ? 'text' : 'password'" 
          placeholder="请输入密码" 
          @confirm="handleLogin"
          @blur="saveFormData"
        />
        <text class="toggle-password" @tap="togglePassword">
          {{ showPassword ? '👁' : '👁‍🗨' }}
        </text>
      </view>
      
      <view class="form-options">
        <label class="checkbox-label">
          <checkbox :checked="rememberPassword" @change="onRememberChange" color="#667eea" />
          <text>记住密码</text>
        </label>
        <text class="forgot-link" @tap="forgotPassword">忘记密码？</text>
      </view>
      
      <button class="btn-login" :class="{ 'loading': loading }" @click="handleLogin">
        {{ loading ? '登录中...' : '登录' }}
      </button>
      
      <view class="quick-login" v-if="!loading">
        <text class="quick-login-title">快速登录</text>
        <view class="quick-login-methods">
          <view class="quick-login-item" @tap="quickLogin('wechat')">
            <text class="quick-icon">💬</text>
          </view>
          <view class="quick-login-item" @tap="quickLogin('qq')">
            <text class="quick-icon">🐧</text>
          </view>
          <view class="quick-login-item" @tap="quickLogin('dingtalk')">
            <text class="quick-icon">💼</text>
          </view>
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
      form: {
        username: '',
        password: ''
      },
      showPassword: false,
      rememberPassword: false,
      loading: false
    }
  },
  onLoad() {
    this.loadSavedData()
  },
  methods: {
    loadSavedData() {
      const savedUsername = uni.getStorageSync('saved_username')
      const savedPassword = uni.getStorageSync('saved_password')
      const rememberPassword = uni.getStorageSync('remember_password')
      
      if (rememberPassword) {
        this.rememberPassword = true
        this.form.username = savedUsername || ''
        this.form.password = savedPassword || ''
      }
    },
    saveFormData() {
      if (this.rememberPassword) {
        uni.setStorageSync('saved_username', this.form.username)
        uni.setStorageSync('saved_password', this.form.password)
      }
    },
    onRememberChange(e) {
      this.rememberPassword = e.detail.value
      uni.setStorageSync('remember_password', this.rememberPassword)
      
      if (!this.rememberPassword) {
        uni.removeStorageSync('saved_username')
        uni.removeStorageSync('saved_password')
      }
    },
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    forgotPassword() {
      uni.showModal({
        title: '重置密码',
        content: '请联系管理员重置密码',
        showCancel: false
      })
    },
    async quickLogin(type) {
      uni.showToast({ 
        title: `${type === 'wechat' ? '微信' : type === 'qq' ? 'QQ' : '钉钉'}登录暂未开放`, 
        icon: 'none' 
      })
    },
    async handleLogin() {
      if (!this.form.username || !this.form.password) {
        uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
        return
      }
      
      if (this.loading) return
      
      this.loading = true
      
      try {
        const res = await api.user.login({
          username: this.form.username,
          password: this.form.password
        })
        
        if (res.code === 200) {
          uni.setStorageSync('token', res.data.token)
          uni.setStorageSync('userInfo', res.data.userInfo)
          
          if (this.rememberPassword) {
            uni.setStorageSync('saved_username', this.form.username)
            uni.setStorageSync('saved_password', this.form.password)
          }
          
          uni.showToast({ title: '登录成功', icon: 'success' })
          
          setTimeout(() => {
            uni.reLaunch({ url: '/pages/home/home' })
          }, 1000)
        }
      } catch (e) {
        console.error(e)
        uni.showToast({ 
          title: e.message || '登录失败，请检查用户名和密码', 
          icon: 'none' 
        })
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style>
.login-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40rpx;
}

.logo {
  margin-bottom: 80rpx;
  text-align: center;
}

.title {
  font-size: 60rpx;
  font-weight: bold;
  color: #fff;
  display: block;
  letter-spacing: 4rpx;
}

.subtitle {
  font-size: 28rpx;
  color: rgba(255,255,255,0.9);
  margin-top: 20rpx;
  display: block;
}

.form {
  width: 100%;
  max-width: 600rpx;
}

.input-wrapper {
  position: relative;
  margin-bottom: 30rpx;
}

.input-icon {
  position: absolute;
  left: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 36rpx;
  z-index: 10;
}

.input {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50rpx;
  padding: 30rpx 30rpx 30rpx 90rpx;
  font-size: 28rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.toggle-password {
  position: absolute;
  right: 30rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 36rpx;
  color: #999;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40rpx;
  font-size: 26rpx;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 10rpx;
  color: rgba(255, 255, 255, 0.9);
}

.forgot-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: underline;
}

.btn-login {
  background: #fff;
  color: #667eea;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 40rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.2);
  transition: all 0.3s;
}

.btn-login.loading {
  opacity: 0.7;
}

.quick-login {
  margin-top: 60rpx;
  text-align: center;
}

.quick-login-title {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.8);
  display: block;
  margin-bottom: 30rpx;
}

.quick-login-methods {
  display: flex;
  justify-content: center;
  gap: 40rpx;
}

.quick-login-item {
  width: 80rpx;
  height: 80rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  backdrop-filter: blur(10rpx);
}
</style>
