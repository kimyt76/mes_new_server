package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class StockVo {

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private String itemTypeName;
    private String spec;
    private String areaCd;
    private String storageCd;
    private String storageName;
    private String testNo;
    private String lotNo;
    private String etc;
    private String endYn;

    private BigDecimal qty;
    private BigDecimal totQty;

    private LocalDate expiryDate;
    private LocalDate inDate;
    private LocalDate outDate;
}
