package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProdOutItemVo {
    private Long tranId;
    private Long tranItemId;

    private String itemCd;
    private String itemTypeCd;
    private String itemName;
    private String testNo;
    private BigDecimal qty;
    private String lotNo;
    private String poNo;
    private String etc;
    private String userId;
}
