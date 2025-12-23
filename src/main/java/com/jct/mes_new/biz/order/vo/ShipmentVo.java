package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentVo {

    private String shipmentId;
    private String shipmentDateSeq;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String shipmentDate;
    private int seq;
    private String itemName;
    private String clientId;
    private String clientName;
    private String managerId;                /*출고요청자*/
    private String managerName;
    private String descStorageCd;
    private String descStorageName;
    private String deliveryManagerName;  /*납품담당자*/
    private String deliveryTelno;       /*납품지연락처*/
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String dueDate;             /*출하예정일*/
    private String releaseTime;    /*출고시간*/
    private String releaseType;    /*출고구분*/
    private String accountStatement; /*거래명세서 */
    private String address;
    private String tradingMethod; /*거래방법*/
    private String attachFileId;
    private String etc;
    private String printYn;
    private String statusType;  /*진행상태*/
    private String saleIds;

    private String poNo;
    private BigDecimal totQty;
    private String userId;
}
