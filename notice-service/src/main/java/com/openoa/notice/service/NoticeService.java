package com.openoa.notice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.openoa.notice.entity.Notice;
import com.openoa.notice.mapper.NoticeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoticeService {
    
    @Autowired
    private NoticeMapper noticeMapper;
    
    public Page<Notice> getPage(Integer pageNum, Integer pageSize, String status, String keyword) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Notice::getStatus, status);
        }
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Notice::getTitle, keyword)
                    .or()
                    .like(Notice::getContent, keyword));
        }
        
        wrapper.orderByDesc(Notice::getCreateTime);
        
        return noticeMapper.selectPage(page, wrapper);
    }
    
    public Notice getById(Long id) {
        return noticeMapper.selectById(id);
    }
    
    public Notice create(Notice notice) {
        notice.setStatus("DRAFT");
        notice.setCreateTime(LocalDateTime.now());
        noticeMapper.insert(notice);
        return notice;
    }
    
    public Notice update(Long id, Notice notice) {
        notice.setId(id);
        noticeMapper.updateById(notice);
        return getById(id);
    }
    
    public Notice publish(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setStatus("PUBLISHED");
            notice.setPublishTime(LocalDateTime.now());
            noticeMapper.updateById(notice);
        }
        return notice;
    }
    
    public Notice withdraw(Long id) {
        Notice notice = getById(id);
        if (notice != null) {
            notice.setStatus("WITHDRAWN");
            noticeMapper.updateById(notice);
        }
        return notice;
    }
    
    public boolean delete(Long id) {
        return noticeMapper.deleteById(id) > 0;
    }
    
    public List<Notice> getLatestPublished(Integer limit) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getStatus, "PUBLISHED")
                .orderByDesc(Notice::getPublishTime)
                .last("LIMIT " + (limit != null ? limit : 5));
        return noticeMapper.selectList(wrapper);
    }
}
