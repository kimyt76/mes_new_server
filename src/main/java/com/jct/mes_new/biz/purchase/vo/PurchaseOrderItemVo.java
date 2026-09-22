package com.jct.mes_new.biz.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderItemVo {
    public Long purOrderItemId;
    public Long purOrderId;
    public String itemTypeCd;
    public String itemCd;
    public String itemName;
    public String spec;
    public BigDecimal qty;
    public BigDecimal inPrice;
    public BigDecimal supplyPrice;
    public BigDecimal vatPrice;
    public String inYn;
    public String etc;
    public String itemMailYn;
    public String itemPrintYn;
    public String itemEndYn;
    public String userId;
}
