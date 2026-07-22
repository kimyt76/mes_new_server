package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MoveItemVo {

    private Long moveStockId;
    private Long moveStockItemId;

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private BigDecimal qty;
    private BigDecimal orderQty;
    private BigDecimal reqQty;
    private String testNo;
    private String lotNo;
    private String poNo;
    private String makeNo;
    private String etc;

    private String userId;
}
