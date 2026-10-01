package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyLaborCostVo {
    private Long dailyCostId;
    private Long dailyId;
    private String procCd;
    private String workTypeCd;
    private String areaCd;
    private String areaName;
    private String orderDist;
    private String itemCd;
    private String lotNo;
    private String customerName;
    private String itemName;
    private String prodType;
    private BigDecimal prodQty;
    private BigDecimal workTime;
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
    private String etc;
    private String userId;
    private BigDecimal manFTotalCost;
    private BigDecimal manDTotalCost;
    private BigDecimal womFTotalCost;
    private BigDecimal womDTotalCost;
    private BigDecimal totalCost;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dailyDate;
    private String endYn;
    private String regId;

}
