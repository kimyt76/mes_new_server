package com.jct.mes_new.biz.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class WorkOrderMstVo {
    // MST
    private Long workOrderId;
    private String workOrderDateSeq;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workOrderDate;
    private Integer seq;
    private String areaCd;
    private String areaName;
    private String clientId;
    private String clientName;
    private String managerId;
    private String managerName;
    private BigDecimal deliveryQty;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryDate;
    private String itemCd;
    private String itemName;
    private String poNo;
    private String etc;
    private String workStatus;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String matOrderDate;

    private Integer batchCnt;
    private String userId;

    private List<WorkOrderVo.Batch> batches;
}
