package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentItemListVo {
    private int rowId;
    private Integer copyNo;
    private Long shipmentItemId;
    private Long shipmentId;
    private Long workProcId;
    private String poNo;
    private String itemCd;
    private String itemTypeCd;
    private String itemName;
    private BigDecimal stockQty;
    private BigDecimal qty;
    private BigDecimal pallet;
    private String lotNo;
    private String makeNo;
    private String storageCd;
    private String storageName;
    private String qcStatus;
    private String testNo;
    private Integer orderDist;

    private String shipmentReqDate;
    private String etc;
    private String userId;
}
