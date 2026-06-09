package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoveItemVo {

    private Long moveReqId;
    private Long moveItemId;
    private Long moveReqItemId;

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private BigDecimal qty;
    private String testNo;
    private String etc;
    private String userId;
}
