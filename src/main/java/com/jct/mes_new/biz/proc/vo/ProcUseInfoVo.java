package com.jct.mes_new.biz.proc.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProcUseInfoVo {

    private Long prodInfoId;
    private Long prodUseId;
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
    private BigDecimal badQty;
    private BigDecimal useQty;
    private BigDecimal totUseQty;
    private BigDecimal workBadQty;
    private String unit;
    private String unitQty;
    private String lotNo;
    private String procCd;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private LocalDate expiryDate;
    private String storageCd;
    private String userId;




}
