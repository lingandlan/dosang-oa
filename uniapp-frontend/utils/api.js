// API 配置
const BASE_URL = 'http://localhost:8080'

// 请求封装
const request = (options) => {
  return new Promise((resolve, reject) => {
    const token = uni.getStorageSync('token')
    
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : '',
        ...options.header
      },
      success: (res) => {
        if (res.data.code === 200) {
          resolve(res.data)
        } else {
          uni.showToast({
            title: res.data.message || '请求失败',
            icon: 'none'
          })
          reject(res.data)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络请求失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

// API 接口
export const api = {
  // 用户相关
  user: {
    list: (params) => request({ url: '/api/v1/users', method: 'GET', data: params }),
    getById: (id) => request({ url: `/api/v1/users/${id}`, method: 'GET' }),
    create: (data) => request({ url: '/api/v1/users', method: 'POST', data }),
    update: (id, data) => request({ url: `/api/v1/users/${id}`, method: 'PUT', data }),
    delete: (id) => request({ url: `/api/v1/users/${id}`, method: 'DELETE' }),
  },
  
  // 审批相关
  approval: {
    list: (params) => request({ url: '/api/v1/approvals', method: 'GET', data: params }),
    getById: (id) => request({ url: `/api/v1/approvals/${id}`, method: 'GET' }),
    create: (data) => request({ url: '/api/v1/approvals', method: 'POST', data }),
    approve: (id, data) => request({ url: `/api/v1/approvals/${id}`, method: 'PUT', data }),
    types: () => request({ url: '/api/v1/approvals/types', method: 'GET' }),
  },
  
  // 考勤相关
  attendance: {
    checkin: (data) => request({ url: '/api/v1/attendance/checkin', method: 'POST', data }),
    today: (userId) => request({ url: '/api/v1/attendance/today', method: 'GET', data: { userId } }),
    list: (params) => request({ url: '/api/v1/attendance', method: 'GET', data: params }),
  },
  
  // 公告相关
  notice: {
    list: (params) => request({ url: '/api/v1/notices', method: 'GET', data: params }),
    getById: (id) => request({ url: `/api/v1/notices/${id}`, method: 'GET' }),
    create: (data) => request({ url: '/api/v1/notices', method: 'POST', data }),
    publish: (id) => request({ url: `/api/v1/notices/${id}/publish`, method: 'PUT' }),
    delete: (id) => request({ url: `/api/v1/notices/${id}`, method: 'DELETE' }),
  },
  
  // 视频相关
  video: {
    createRoom: (data) => request({ url: '/api/v1/video/room', method: 'POST', data }),
    getRoom: (roomId) => request({ url: `/api/v1/video/room/${roomId}`, method: 'GET' }),
    getConfig: () => request({ url: '/api/v1/video/config', method: 'GET' }),
  },
}

export default api
