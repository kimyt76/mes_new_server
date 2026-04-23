package com.jct.mes_new.biz.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WorkOrderInfoVo {
    private Long workOrderId;
    private Long workBatchId;
    private Long workProcId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workOrderDate;
    private Integer seq;
    private String workOrderDateSeq;
    private String areaCd;
    private String areaName;
    private String clientId;
    private String clientName;
    private String managerId;
    private String managerName;
    private String poNo;

    private String makeNo;
    private String lotNo;
    private String lotNo2;
    private String batchStatus;

    private String procCd;
    private String itemCd;
    private String itemName;
    private String testNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;
    private BigDecimal orderQty;
    private BigDecimal totUseQty;
    private BigDecimal prodQty;
    private BigDecimal makeQty;
    private BigDecimal prodYield;
    private String procStatus;
    private String procStatusName;
    private String storageCd;
    private String storageName;
    private String memo;
    private String etc;
    private String tranYn;
    private String endYn;

}
