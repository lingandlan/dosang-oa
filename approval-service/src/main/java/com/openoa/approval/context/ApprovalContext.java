package com.openoa.approval.context;

import com.openoa.approval.entity.ApprovalInstance;
import com.openoa.approval.entity.ApprovalNode;
import com.openoa.approval.entity.ApprovalRecord;
import lombok.Data;
import java.util.List;

@Data
public class ApprovalContext {
    private ApprovalInstance instance;
    private ApprovalNode currentNode;
    private List<ApprovalNode> nodes;
    private ApprovalRecord record;
    private String comment;
    private Long operatorId;
    private String operatorName;
}
