package com.openoa.user.dto;

public class LoginResponse {
    private String token;
    private UserInfo user;
    
    public LoginResponse() {}
    
    public LoginResponse(String token, UserInfo user) {
        this.token = token;
        this.user = user;
    }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    
    public static class UserInfo {
        private Long id;
        private String username;
        private String realName;
        private String email;
        private String phone;
        private Long departmentId;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String username, String realName, String email, String phone, Long departmentId) {
            this.id = id;
            this.username = username;
            this.realName = realName;
            this.email = email;
            this.phone = phone;
            this.departmentId = departmentId;
        }
        
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRealName() { return realName; }
        public void setRealName(String realName) { this.realName = realName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
    }
}
