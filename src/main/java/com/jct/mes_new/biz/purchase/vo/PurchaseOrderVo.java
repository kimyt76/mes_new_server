package com.jct.mes_new.biz.purchase.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
public class PurchaseOrderVo {
    private Long purOrderId;
    private Long purOrderItemId;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String purOrderDate;
    private Integer seq;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String strDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String toDate;
    private String storageCd;
    private String storageName;
    private String areaCd;
    private String customerCd ;
    private String customerName ;
    private String managerId;
    private String managerName;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String deliveryDate;
    private String customerManagerName;
    private String remark;
    private String vatType;
    private String mailYn;
    private String printYn;
    private String endYn;
    private String inYn;
    private String itemTypeCd;
    public String itemCd;
    public String itemName;

    private String userId;

    List<PurchaseOrderItemVo> purchaseOrderItemList;
    List<PurchaseOrderListVo> purchaseOrderList;

    @Data
    public static class PurchaseOrderItemVo {
        public Long purOrderItemId;
        public Long purOrderId;
        public String itemTypeCd;
        public String itemCd;
        public String itemName;
        public String spec;
        public BigDecimal qty;
        public BigDecimal inPrice;
        public BigDecimal supplyPrice;
        public BigDecimal vatPrice;
        public String inYn;
        public String orderState;
        public String endYn;
        public String etc;
        public String userId;
    }

    @Data
    public static class PurchaseOrderListVo {
        public Long purOrderItemId;
        public Long purOrderId;
        public String itemTypeCd;
        private String purOrderDateSeq;
        private String purOrderDate;
        private String deliveryDate;
        private String customerName;
        public String itemTypeName;
        private String storageName;
        public String itemCd;
        public String itemName;
        public String spec;
        public BigDecimal qty;
        public BigDecimal totQty;
        public BigDecimal inQty;
        public BigDecimal inPrice;
        public BigDecimal supplyPrice;
        public BigDecimal vatPrice;
        public String inYn;
        public String orderState;
        public String endYn;
        private String mailYn;
        public String managerName;
    }
}
