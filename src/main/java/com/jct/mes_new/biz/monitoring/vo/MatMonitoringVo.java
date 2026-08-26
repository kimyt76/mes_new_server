package com.jct.mes_new.biz.monitoring.vo;

import lombok.Data;

import java.math.BigDecimal;

import static org.apache.coyote.http11.Constants.a;

@Data
public class MatMonitoringVo {
   private String areaName;
   private String equipmentCd;
   private String equipmentName;
   private String itemCd;
   private String itemName;
   private BigDecimal prodQty;
   private Long workProcId;
}
