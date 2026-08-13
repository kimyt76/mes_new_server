package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RealStockItemVo {

    private Long realStockMstId;
    private Long realStockItemId;
    private String itemCd;
    private String itemName;
    private String testNo;
    private BigDecimal docStockQty;
    private BigDecimal reqlStockQty;
    private BigDecimal diffStockQty;
    private String etc;

    private String userId;

}
