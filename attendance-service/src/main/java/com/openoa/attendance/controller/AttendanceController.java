package com.openoa.attendance.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.attendance.entity.AttendanceRecord;
import com.openoa.attendance.mapper.AttendanceRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/attendance")
public class AttendanceController {
    
    @Autowired
    private AttendanceRecordMapper recordMapper;
    
    // 打卡
    @PostMapping("/checkin")
    public Map<String, Object> checkin(
            @RequestParam Long userId,
            @RequestParam String checkType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String device) {
        
        AttendanceRecord record = new AttendanceRecord();
        record.setUserId(userId);
        record.setCheckType(checkType);
        record.setCheckTime(LocalDateTime.now());
        record.setLocation(location);
        record.setDevice(device);
        recordMapper.insert(record);
        
        return Map.of("code", 200, "message", "打卡成功", "data", record);
    }
    
    // 今日考勤
    @GetMapping("/today")
    public Map<String, Object> today(@RequestParam Long userId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AttendanceRecord::getUserId, userId)
               .between(AttendanceRecord::getCheckTime, startOfDay, endOfDay)
               .orderByAsc(AttendanceRecord::getCheckTime);
        
        var records = recordMapper.selectList(wrapper);
        return Map.of("code", 200, "message", "success", "data", records);
    }
    
    // 考勤记录列表
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String checkType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        
        Page<AttendanceRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AttendanceRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) wrapper.eq(AttendanceRecord::getUserId, userId);
        if (checkType != null) wrapper.eq(AttendanceRecord::getCheckType, checkType);
        if (date != null) {
            wrapper.between(AttendanceRecord::getCheckTime, date.atStartOfDay(), date.plusDays(1).atStartOfDay());
        }
        wrapper.orderByDesc(AttendanceRecord::getCheckTime);
        
        Page<AttendanceRecord> result = recordMapper.selectPage(page, wrapper);
        return Map.of("code", 200, "message", "success", "data", result);
    }
}
