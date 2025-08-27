package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProgressVo {

    private String itemCd;
    private String itemName;
    private String customerCd;
    private String customerName;
    private String contractDate;
    private String saleDate;
    private String shipmentDate;
    private BigDecimal totQty;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal supplyPrice;
    private BigDecimal vatPrice;
    private BigDecimal totPrice;
    private String managerName;
    private String managerId;
    private String tradingMethod;
}
