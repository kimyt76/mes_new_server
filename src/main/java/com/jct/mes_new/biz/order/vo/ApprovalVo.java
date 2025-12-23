package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
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
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String businessApprovalDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String productApprovalDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String purchaseApprovalDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String qcApprovalDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String labApprovalDate;
    private String businessCheckYn;
    private String productCheckYn;
    private String purchaseCheckYn;
    private String qcCheckYn;
    private String labCheckYn;

    private String userId;
}
