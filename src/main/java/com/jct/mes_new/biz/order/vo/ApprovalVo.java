package com.jct.mes_new.biz.order.vo;

import lombok.Data;

@Data
public class ApprovalVo {
    private String businessUserId;
    private String productUserId;
    private String purchaseUserId;
    private String qcUserId;
    private String labUserId;
    private String businessUserName;
    private String productUserName;
    private String purchaseUserName;
    private String qcUserName;
    private String labUserName;
    private String businessApprovalDate;
    private String productApprovalDate;
    private String purchaseApprovalDate;
    private String qcApprovalDate;
    private String labApprovalDate;
    private String businessCheckYn;
    private String productCheckYn;
    private String purchaseCheckYn;
    private String qcCheckYn;
    private String labCheckYn;

    private String userId;
}
