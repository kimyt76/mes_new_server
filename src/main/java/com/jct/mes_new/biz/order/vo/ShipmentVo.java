package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ShipmentVo {
    private Long shipmentId;
    private String shipmentDateSeq;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shipmentDate;
    private int seq;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shipmentReqDate;     /* 출고요정일*/
    private String shipmentTime;
    private String clientId;
    private String deliveryLocation;
    private String deliveryAddress;
    private String deliveryManagerName;
    private String deliveryTelno;
    private String shipmentType;
    private String shipmentYn;
    private String managedItem;
    private String attachFileId;
    private String shipmentStatus;
    private String etc;
    private String printYn;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strReqDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endReqDate;
    private String poNo;
    private String itemCd;
    private String itemName;
    private String clientName;
    private String businessManagerName;
    private String shipmentStatusName;
    private String shipmentTypeName;
    private BigDecimal qty;
    private Integer lotCnt;
    private BigDecimal pallet;

    private String userId;


}
