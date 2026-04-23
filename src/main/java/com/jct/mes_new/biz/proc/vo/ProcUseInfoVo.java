package com.jct.mes_new.biz.proc.vo;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcUseInfoVo {

    private Long prodInfoId;
    private Long workProcId;
    private Long workBatchId;

    private String itemTypeCd;
    private String itemTypeName;
    private String itemCd;
    private String itemName;
    private String matName;
    private String testNos;
    private String specInfo;
    private String testNo;
    private String exAppearance;
    private String packingSpecValue;
    private String packingSpecUnit;
    private BigDecimal reqQty;
    private BigDecimal totUseQty;
    private BigDecimal badQty;
    private BigDecimal useQty;
    private BigDecimal workBadQty;
    private String unit;
    private String unitQty;
    private String lotNo;
    private String userId;




}
