package com.openoa.notice.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.notice.entity.Notice;
import com.openoa.notice.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    // 获取公告列表
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status) {
        
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, "PUBLISHED")
               .orderByDesc(Notice::getPublishTime);
        
        if (status != null) wrapper.eq(Notice::getStatus, status);
        wrapper.orderByDesc(Notice::getCreateTime);
        
        Page<Notice> result = noticeMapper.selectPage(page, wrapper);
        return Map.of("code", 200, "message", "success", "data", result);
    }
    
    // 获取公告详情
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Notice notice = noticeMapper.selectById(id);
        return Map.of("code", 200, "message", "success", "data", notice);
    }
    
    // 发布公告
    @PostMapping
    public Map<String, Object> create(@RequestBody Notice notice) {
        notice.setStatus("DRAFT");
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        return Map.of("code", 200, "message", "创建成功", "data", notice);
    }
    
    // 发布公告
    @PutMapping("/{id}/publish")
    public Map<String, Object> publish(@PathVariable Long id) {
        Notice notice = noticeMapper.selectById(id);
        notice.setStatus("PUBLISHED");
        notice.setPublishTime(LocalDateTime.now());
        noticeMapper.updateById(notice);
        return Map.of("code", 200, "message", "发布成功");
    }
    
    // 删除公告
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        noticeMapper.deleteById(id);
        return Map.of("code", 200, "message", "删除成功");
    }
}
