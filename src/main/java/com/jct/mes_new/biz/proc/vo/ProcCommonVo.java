package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ProcCommonVo {

    private BigInteger equipmentId;
    private String equipmentCd;
    private String equipmentName;
    private String etc;


    private Long workOrderId;
    private Long workBatchId;
    private Long workProcId;
    private String storageCd;
    private String procStatus;
    private String batchStatus;

    private String userId;

}
