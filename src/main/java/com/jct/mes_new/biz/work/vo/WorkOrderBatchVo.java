package com.jct.mes_new.biz.work.vo;

import lombok.Data;

import java.util.List;

@Data
public class WorkOrderBatchVo {
    private Long workBatchId;
    private Long workOrderId;
    private String poNo;
    private String makeNo;
    private String lotNo;
    private String lotNo2;
    private String batchStatus;
    private Integer orderDist;

    private String userId;

    private List<WorkOrderVo.Item> items;
}
