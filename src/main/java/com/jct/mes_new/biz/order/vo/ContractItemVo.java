package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContractItemVo {
    private String contractItemId;
    private String contractId;
    private String poNo;
    private String itemCd;
    private String itemName;
    private String unit;
    private String orderCnt;        /*수주차수*/
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal supplyPrice;
    private BigDecimal vatPrice;
    private BigDecimal totPrice;

    private String prodType;    /* 제품유형*/
    private String statusType;
    private String etc;
    private int orderDist;


    private String userId;
}
