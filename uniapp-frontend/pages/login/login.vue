<template>
  <view class="login-container">
    <view class="logo">
      <text class="title">OpenOA</text>
      <text class="subtitle">企业办公平台</text>
    </view>
    
    <view class="form">
      <input 
        class="input" 
        v-model="form.username" 
        placeholder="请输入用户名" 
      />
      <input 
        class="input" 
        v-model="form.password" 
        type="password" 
        placeholder="请输入密码" 
        @confirm="handleLogin"
      />
      <button class="btn-login" @click="handleLogin">登录</button>
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
      }
    }
  },
  methods: {
    async handleLogin() {
      if (!this.form.username || !this.form.password) {
        uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
        return
      }
      
      try {
        // 这里简化处理，实际应该调用登录接口
        uni.setStorageSync('token', 'mock-token')
        uni.setStorageSync('userInfo', { username: this.form.username })
        
        uni.showToast({ title: '登录成功', icon: 'success' })
        
        setTimeout(() => {
          uni.reLaunch({ url: '/pages/home/home' })
        }, 1000)
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>

<style>
.login-container {
  height: 100vh;
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
}

.subtitle {
  font-size: 28rpx;
  color: rgba(255,255,255,0.8);
}

.form {
  width: 100%;
}

.input {
  background: #fff;
  border-radius: 50rpx;
  padding: 30rpx 40rpx;
  margin-bottom: 30rpx;
  font-size: 28rpx;
}

.btn-login {
  background: #fff;
  color: #667eea;
  border-radius: 50rpx;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 40rpx;
}
</style>
