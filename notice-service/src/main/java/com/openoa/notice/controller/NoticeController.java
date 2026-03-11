package com.openoa.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.notice.entity.Notice;
import com.openoa.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        
        Page<Notice> result = noticeService.getPage(pageNum, pageSize, status, keyword);
        return Map.of("code", 200, "message", "success", "data", result);
    }
    
    @GetMapping("/latest")
    public Map<String, Object> getLatest(@RequestParam(defaultValue = "5") Integer limit) {
        return Map.of("code", 200, "message", "success", "data", noticeService.getLatestPublished(limit));
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        return Map.of("code", 200, "message", "success", "data", notice);
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody Notice notice) {
        return Map.of("code", 200, "message", "创建成功", "data", noticeService.create(notice));
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Notice notice) {
        return Map.of("code", 200, "message", "更新成功", "data", noticeService.update(id, notice));
    }
    
    @PutMapping("/{id}/publish")
    public Map<String, Object> publish(@PathVariable Long id) {
        noticeService.publish(id);
        return Map.of("code", 200, "message", "发布成功");
    }
    
    @PutMapping("/{id}/withdraw")
    public Map<String, Object> withdraw(@PathVariable Long id) {
        noticeService.withdraw(id);
        return Map.of("code", 200, "message", "撤回成功");
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        noticeService.delete(id);
        return Map.of("code", 200, "message", "删除成功");
    }
}
