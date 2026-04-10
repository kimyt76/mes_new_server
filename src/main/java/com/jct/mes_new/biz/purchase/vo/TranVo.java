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
    private String poNo;
    private String remark;
    private String tranTypeCd;
    private String tranStatus;

    private String userId;

    List<searchTranListVo> searchTranList;

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
