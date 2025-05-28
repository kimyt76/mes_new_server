package com.jct.mes_new.biz.order.vo;

import lombok.Data;

@Data
public class ApprovalVo {
    private String businessUserId;
    private String productUserId;
    private String purchaseUserId;
    private String qcUserId;
    private String labUserId;
    private String businessApprovalDate;
    private String productIdApprovalDate;
    private String purchaseIdApprovalDate;
    private String qcApprovalDate;
    private String labApprovalDate;

    private String field;

    private String approvalId;
    private String type;
    private String approvalUserId;
    private String approvalDate;
    private String approvalTxt;



}
