package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class ProcCommonVo {

    private BigInteger equipmentId;
    private String equipmentCd;
    private String equipmentName;

    private Long workOrderId;
    private Long workBatchId;
    private Long workProcId;
    private String storageCd;
    private String procStatus;
    private String batchStatus;
    private String itemCd;
    private String procCd;

    private String workerName;
    private String etc;

    private String bagWeightName;
    private BigDecimal weight;

    private String userId;

}
