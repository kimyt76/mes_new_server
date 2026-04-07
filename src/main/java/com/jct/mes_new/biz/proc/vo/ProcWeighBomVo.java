package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class ProcWeighBomVo {
    private long weighId;
    private long workProcId;

    private String itemCd;
    private String itemName;
    private String phase;
    private String appearance;
    private BigDecimal realContents;
    private BigDecimal orderQty;
    private BigDecimal weighQty;
    private BigDecimal bagWeight;
    private BigDecimal totQty;
    private String testNo;
    private String weighYn;
    private String weigher;
    private String confirmer;
    private BigInteger distOrder;
    private BigInteger rowNum;

    private String userId;

}
