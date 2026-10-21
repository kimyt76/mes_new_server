package com.jct.mes_new.biz.work.vo;

import lombok.Data;

import java.util.List;

@Data
public class WorkOrderResetRequestVo {
    private Long workOrderId;
    private String procCd;
    private Long workProcId;
    private List<Long> workProcIds;
    private String userId;
}
