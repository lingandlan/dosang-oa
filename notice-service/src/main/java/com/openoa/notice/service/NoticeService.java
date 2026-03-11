package com.openoa.notice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openoa.notice.entity.Notice;
import com.openoa.notice.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    public Page<Notice> pageList(Integer pageNum, Integer pageSize, String status, String category, Integer pinned, String scope) {
        Page<Notice> page = new Page<>(pageNum, pageSize);
        lambdaQuery()
                .eq(status != null, Notice::getStatus, status)
                .eq(category != null, Notice::getCategory, category)
                .eq(pinned != null, Notice::getPinned, pinned)
                .eq(scope != null, Notice::getScope, scope)
                .orderByDesc(Notice::getPinned)
                .orderByDesc(Notice::getPublishTime)
                .orderByDesc(Notice::getCreateTime)
                .page(page);
        return page;
    }
}