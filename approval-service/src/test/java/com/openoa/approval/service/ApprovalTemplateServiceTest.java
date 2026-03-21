package com.openoa.approval.service;

import com.openoa.approval.entity.ApprovalTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ApprovalTemplateServiceTest {

    @Autowired
    private ApprovalTemplateService templateService;

    @BeforeEach
    public void setUp() {
        // 清理测试数据
        // 注意：这里需要通过mapper清理
    }

    @Test
    public void testGetAllTemplates() {
        List<ApprovalTemplate> templates = templateService.getAllTemplates();

        assertNotNull(templates);
        // 验证初始化数据
        assertTrue(templates.size() >= 3);
    }

    @Test
    public void testGetById() {
        ApprovalTemplate template = templateService.getById(1L);

        assertNotNull(template);
        assertEquals("普通请假", template.getTemplateName());
    }

    @Test
    public void testCreateTemplate() {
        ApprovalTemplate template = new ApprovalTemplate();
        template.setTemplateName("新请假模板");
        template.setTemplateCode("LEAVE_NEW");
        template.setTypeId(1L);
        template.setDescription("新建的请假模板");
        template.setIsActive(true);

        ApprovalTemplate created = templateService.createTemplate(template);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("新请假模板", created.getTemplateName());
    }

    @Test
    public void testUpdateTemplate() {
        // 先创建模板
        ApprovalTemplate template = new ApprovalTemplate();
        template.setTemplateName("原始模板");
        template.setTemplateCode("ORIGINAL_TEMPLATE");
        template.setTypeId(1L);
        template.setDescription("原始描述");
        template.setIsActive(true);
        templateService.createTemplate(template);

        Long templateId = template.getId();

        // 更新模板
        ApprovalTemplate updateTemplate = new ApprovalTemplate();
        updateTemplate.setId(templateId);
        updateTemplate.setTemplateName("更新后的模板");
        updateTemplate.setTemplateCode("ORIGINAL_TEMPLATE");
        updateTemplate.setTypeId(1L);
        updateTemplate.setDescription("更新后的描述");
        updateTemplate.setIsActive(true);
        templateService.updateTemplate(updateTemplate);

        ApprovalTemplate updated = templateService.getById(templateId);

        assertEquals("更新后的模板", updated.getTemplateName());
        assertEquals("更新后的描述", updated.getDescription());
    }

    @Test
    public void testDeleteTemplate() {
        // 先创建模板
        ApprovalTemplate template = new ApprovalTemplate();
        template.setTemplateName("待删除模板");
        template.setTemplateCode("TO_DELETE");
        template.setTypeId(1L);
        template.setDescription("将被删除");
        template.setIsActive(true);
        templateService.createTemplate(template);

        Long templateId = template.getId();

        // 删除模板
        templateService.deleteTemplate(templateId);

        ApprovalTemplate deleted = templateService.getById(templateId);

        assertNull(deleted);
    }

    @Test
    public void testGetByIdNotFound() {
        ApprovalTemplate template = templateService.getById(99999L);

        assertNull(template);
    }
}
