package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class ProcWeighBomVo {
    private long weighId;
    private long workProcId;
    private long workBatchId;

    private String itemCd;
    private String itemName;
    private String phase;
    private String appearance;
    private BigDecimal orderQty;
    private BigDecimal weighQty;
    private BigDecimal bagWeight;
    private BigDecimal totQty;
    private String testNo;
    private String weighYn;
    private String weigher;
    private String confirmer;

    /* 제조 추가*/
    private String makeTime;
    private String makeYn;
    private String maker;
    private String makeConfirmer;
    private String makeQty;

    private BigInteger orderDist;

    private String userId;

}
