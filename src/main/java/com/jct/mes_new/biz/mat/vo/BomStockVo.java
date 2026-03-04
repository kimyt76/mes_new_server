package com.jct.mes_new.biz.mat.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BomStockVo {

    private String bomId;
    private String itemCd;
    private String itemName;
    private String spec;
    private BigDecimal stockQty;
    private BigDecimal reqQty;
    private BigDecimal orderQty;
    private BigDecimal minQty;
    private String customerCd;
    private String customerName;
    private String inOut;
    private BigDecimal inPrice;
}
