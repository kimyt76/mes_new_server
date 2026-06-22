package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MoveStockItemVo {

    private Long moveStockId;
    private Long moveStockItemId;

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private String testNo;
    private BigDecimal qty;
    private BigDecimal orderQty;
    private String etc;
    private String poNo;
    private String makeNo;
    private String userId;
}
