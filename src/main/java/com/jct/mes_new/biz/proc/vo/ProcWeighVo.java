package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class ProcWeighVo {

    private String workOrderId;
    private String workBatchId;
    private String workProcId;

    private String areaCd;
    private String areaName;
    private String poNo;
    private String workOrderDate;
    private String matNo;
    private String lotNo;
    private String lotNo2;
    private String itemCd;
    private String itemName;
    private BigDecimal orderQty;
    private String workStatus;
    private String procCd;


    private String userId;
}
