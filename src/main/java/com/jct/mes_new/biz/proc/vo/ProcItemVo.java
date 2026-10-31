package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcItemVo {

    private Long moveStockId;
    private Long moveReqItemId;

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private BigDecimal qty;
    private BigDecimal prodQty;
    private BigDecimal orderQty;
    private String etc;
    private String poNo;
    private String makeNo;
    private String userId;
}
