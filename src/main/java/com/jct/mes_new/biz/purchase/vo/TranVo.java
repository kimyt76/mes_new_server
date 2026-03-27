package com.jct.mes_new.biz.purchase.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class TranVo {
    private Long tranId;
    private Long purId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tranDate;
    private String areaCd;
    private String fromStorageCd;
    private String fromStorageName;
    private String toStorageCd;
    private String toStorageName;
    private String customerCd;
    private String customerName;
    private String managerId;
    private String managerName;
    private String orderType;
    private String vatType;
    private String endYn;
    private String remark;
    private String tranTypeCd;
    private String tranStatus;

    private String userId;

    List<TranItemListVo> tranItemList;
    List<searchTranListVo> searchTranList;

    @Data
    public static class TranItemListVo {
        private Long tranId;
        private Long tranItemId;
        private Long purItemId;
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
    public static class searchTranListVo {
        private Long tranId;
        private String purDateSeq;
        private String itemTypeCd;
        private String itemCd;
        private String itemName;
        private BigDecimal totQty;
        private BigDecimal totPrice;
        private String toStorageName;
        private String orderType;
        private String managerName;
        private String customerName;
    }



}
