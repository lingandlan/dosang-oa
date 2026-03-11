package com.openoa.approval.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.openoa.approval.entity.ApprovalNode;
import com.openoa.approval.mapper.ApprovalNodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/approval-nodes")
public class ApprovalNodeController {
    
    @Autowired
    private ApprovalNodeMapper nodeMapper;
    
    @GetMapping("/template/{templateId}")
    public Map<String, Object> getByTemplateId(@PathVariable Long templateId) {
        LambdaQueryWrapper<ApprovalNode> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ApprovalNode::getTemplateId, templateId);
        wrapper.orderByAsc(ApprovalNode::getSequence);
        List<ApprovalNode> nodes = nodeMapper.selectList(wrapper);
        return Map.of("code", 200, "message", "success", "data", nodes);
    }
    
    @PostMapping
    public Map<String, Object> create(@RequestBody ApprovalNode node) {
        nodeMapper.insert(node);
        return Map.of("code", 200, "message", "创建成功", "data", node);
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody ApprovalNode node) {
        node.setId(id);
        nodeMapper.updateById(node);
        return Map.of("code", 200, "message", "更新成功", "data", node);
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        nodeMapper.deleteById(id);
        return Map.of("code", 200, "message", "删除成功");
    }
}
