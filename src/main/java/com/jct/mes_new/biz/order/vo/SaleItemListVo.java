package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleItemListVo {
    private String saleItemId;
    private String saleId;
    private String itemCd;
    private String itemName;
    private String unit;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal supplyPrice;
    private BigDecimal vatPrice;
    private String etc;
    private String serialLot;

    private String contractDateSeq;
    private String contractId;
    private String contractItemId;
    private int orderDist;
    private int id;
    private String userId;
}
