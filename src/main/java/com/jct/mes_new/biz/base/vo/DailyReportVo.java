package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DailyReportVo {

    private Long dailyId;
    private Long dailyItemId;     /* 입고, 반출, 불량, 외부출고 id*/
    private Long dailyProdId;     /* 제품 소요량 id*/
    private Long dailyItemUseId;     /* 원료 사용량 id*/

    private Long dailySubItemId;     /* 부자재 입고,반출, 불량 id*/
    private Long dailySubUseId;     /* 부자재 사용량 id*/

    private Long dailyPackingId;     /* 부자재 사용량 id*/

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dailyDate;

    private Integer orderDist;

    private String typeCd;
    private String itemCd;
    private String subItemCd;
    private String itemName;
    private String packingItemName;
    private String bomName;
    private String customerName;
    private String areaName;
    private String storageName;
    private String spec;
    private String lotNo;
    private String expiryDate;
    private String endYn;
    private String etc;
    private String bomItemName;
    private String tranTypeCd;
    private String itemTypeCd;

    private BigDecimal inPrice;
    private BigDecimal supplyPrice;
    private BigDecimal requiredQuantity;  /* 소요량*/
    private BigDecimal qty;
    private BigDecimal orderQty;    /* 지시수량*/
    private BigDecimal prodQty;    /* 지시수량*/

    private BigDecimal badQty;      /* 원불량 */
    private BigDecimal workBadQty;  /* 작업불량 */

    private BigDecimal unitPrice;    /* g당 또는 개당 단가*/
    private BigDecimal totalPrice;
    private BigDecimal sumPrice;

    private String packingItemCd;
    private String regId;

    private BigDecimal inQty;    /* 지시수량*/
    private BigDecimal returnQty;    /* 지시수량*/
    private BigDecimal discardQty;    /* 지시수량*/
    private BigDecimal useQty;    /* 지시수량*/
    private BigDecimal ospQty;    /* 지시수량*/
    private BigDecimal shipmentQty;    /* 지시수량*/

    private String userId;

}
