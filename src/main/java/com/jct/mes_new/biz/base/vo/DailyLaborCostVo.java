package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DailyLaborCostVo {
    private Long dailyCostId;
    private Long dailyId;
    private String procCd;
    private String workTypeCd;
    private String orderDist;
    private String itemCd;
    private String itemName;
    private String prodType;
    private BigDecimal prodQty;
    private String workTime;
    private Integer manFCnt;
    private Integer manDCnt;
    private Integer womFCnt;
    private Integer womDCnt;

    private BigDecimal totLaborPrice;

    private Long dailyLaborCostId;
    private Integer manFCost;
    private Integer manDCost;
    private Integer womFCost;
    private Integer womDCost;
    private String defaultYn;
    private String userId;

}
