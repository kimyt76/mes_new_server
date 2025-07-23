package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemListVo {
    private long contractItemId;
    private String contractId;
    private String itemCd;
    private String itemName;
    private String unit;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal supplyPrice;
    private BigDecimal vatPrice;
    private String etc;

    private String id;
    private String userId;
}
