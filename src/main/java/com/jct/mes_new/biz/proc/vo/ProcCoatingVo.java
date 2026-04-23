package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcCoatingVo {

    private Long workProcId;
    private Long workBatchId;
    private Long workOrderId;

    private String areaCd;
    private String areaName;
    private String poNo;
    private String procOrderDate;
    private String makeNo;
    private String lotNo;
    private String itemCd;
    private String itemName;
    private BigDecimal orderQty;
    private String moveReqYn;
    private String batchStatus;
    private String batchStatusName;
    private String procStatus;
    private String procStatusName;
    private String storageName;
    private String storageCd;

    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String strDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String endDate;

    private String testNo;
    private String procCd;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String prodDate;
    private String workStartTime;
    private String workEndTime;
    private String managerId;
    private String userId;


}
