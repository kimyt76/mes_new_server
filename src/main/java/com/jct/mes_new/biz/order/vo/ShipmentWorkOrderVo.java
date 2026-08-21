package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentWorkOrderVo {
    private int rowId;
    private Long shipmentItemId;
    private Long shipmentId;
    private Long workProcId;
    private String poNo;
    private String itemTypeCd;
    private String itemTypeName;
    private String itemCd;
    private String itemName;
    private String lotNo;
    private String makeNo;
    private BigDecimal stockQty;
    private BigDecimal pallet;
    private String storageCd;
    private String storageName;
    private String passState;
    private String passStateName;
    private String procCd;
    private String procStatus;
    private String testNo;
    private String userId;
}
