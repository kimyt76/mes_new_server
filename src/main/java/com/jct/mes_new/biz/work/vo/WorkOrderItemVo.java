package com.jct.mes_new.biz.work.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class WorkOrderItemVo {
    private Long workProcId;
    private Long workBatchId;
    private Long workOrderId;
    private Long inTranId;
    private Long outTranId;

    private String poNo;
    private String procCd;
    private String itemCd;
    private String itemName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate procOrderDate;
    private BigDecimal orderQty;
    private String procStatus;
    private String procStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;
    private String storageCd;
    private String storageName;
    private String memo;
    private String etc;

    private String managerId;

    private String userId;
}
