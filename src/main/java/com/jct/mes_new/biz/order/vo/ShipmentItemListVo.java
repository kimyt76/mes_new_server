package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentItemListVo {

    private String shipmentItemId;
    private String shipmentId;
    private String itemCd;
    private String itemName;
    private BigDecimal qty;
    private String lotNo;
    private String saleId;
    private String saleItemId;
    private String orderDist;

    private String saleDateSeq;
    private String userId;
    private int id;
}
