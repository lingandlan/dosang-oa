package com.openoa.notice.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.notice.entity.Notice;
import com.openoa.notice.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer pinned,
            @RequestParam(required = false) String scope) {

        Page<Notice> result = noticeService.pageList(pageNum, pageSize, status, category, pinned, scope);
        return Map.of("code", 200, "message", "success", "data", result);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        return Map.of("code", 200, "message", "success", "data", notice);
    }

    @PostMapping
    public Map<String, Object> create(@RequestBody Notice notice) {
        notice.setStatus("DRAFT");
        notice.setCreateTime(LocalDateTime.now());
        noticeService.save(notice);
        return Map.of("code", 200, "message", "创建成功", "data", notice);
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Notice notice) {
        notice.setId(id);
        noticeService.updateById(notice);
        return Map.of("code", 200, "message", "更新成功", "data", notice);
    }

    @PutMapping("/{id}/publish")
    public Map<String, Object> publish(@PathVariable Long id) {
        Notice notice = noticeService.getById(id);
        notice.setStatus("PUBLISHED");
        notice.setPublishTime(LocalDateTime.now());
        noticeService.updateById(notice);
        return Map.of("code", 200, "message", "发布成功");
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Map.of("code", 200, "message", "删除成功");
    }
}
