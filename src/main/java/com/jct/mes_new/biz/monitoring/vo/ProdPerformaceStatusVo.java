package com.jct.mes_new.biz.monitoring.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProdPerformaceStatusVo {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

     private String areaName;
     private String areaCd;
     private String itemCd;
     private String itemName;
     private String makeNo;
     private String lotNo;
     private String lotNo2;
     private String batchStatus;
     private BigDecimal prodQty;
     private BigDecimal coatingQty;
     private BigDecimal chargingQty;
     private BigDecimal packingQty;
}
