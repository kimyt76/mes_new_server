package com.jct.mes_new.biz.purchase.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseVo {
    private Long purId;
    private Long purOrderId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purDate;
    private Integer seq;
    private String areaCd;
    private String itemTypeCd;
    private String itemCd;
    private String itemName;
    private String storageCd;
    private String storageName;
    private String customerCd;
    private String customerName;
    private String managerId;
    private String managerName;
    private String orderType;
    private String vatType;
    private String endYn;
    private String remark;

    private String userId;

    List<PurchaseListVo> purchaseList;
    List<searchPurchaseListVo> searchPurchaseList;

    @Data
    public static class PurchaseListVo {
        private Long purId;
        private Long purItemId;
        private Long purOrderItemId;
        private Long purOrderId;
        private Long tranItemId;
        private Long tranId;
        private String itemTypeCd;
        private String itemCd;
        private String itemName;
        private String spec;
        private String unit;
        private BigDecimal qty;
        private BigDecimal inPrice;
        private BigDecimal supplyPrice;
        private BigDecimal vatPrice;
        private String lotNo;
        private String testNo;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private LocalDate expiryDate;
        private String inYn;
        private String qcStatus;
        private String etc;

        private String userId;
    }

    @Data
    public static class searchPurchaseListVo {
        private Long purId;
        private String purDateSeq;
        private String itemTypeCd;
        private String itemCd;
        private String itemName;
        private BigDecimal totQty;
        private BigDecimal totPrice;
        private String storageName;
        private String orderType;
        private String managerName;
        private String customerName;
    }



}
