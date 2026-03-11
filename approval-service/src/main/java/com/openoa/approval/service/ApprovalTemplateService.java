package com.openoa.approval.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openoa.approval.entity.ApprovalTemplate;
import com.openoa.approval.mapper.ApprovalTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApprovalTemplateService {
    
    @Autowired
    private ApprovalTemplateMapper templateMapper;
    
    public List<ApprovalTemplate> getAllTemplates() {
        LambdaQueryWrapper<ApprovalTemplate> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalTemplate::getIsActive, true);
        return templateMapper.selectList(wrapper);
    }
    
    public ApprovalTemplate getById(Long id) {
        return templateMapper.selectById(id);
    }
    
    public ApprovalTemplate createTemplate(ApprovalTemplate template) {
        templateMapper.insert(template);
        return template;
    }
    
    public ApprovalTemplate updateTemplate(ApprovalTemplate template) {
        templateMapper.updateById(template);
        return template;
    }
    
    public void deleteTemplate(Long id) {
        templateMapper.deleteById(id);
    }
}
