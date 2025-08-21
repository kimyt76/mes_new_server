package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShipmentVo {

    private String shipmentId;
    private String shipmentDateSeq;
    private String shipmentDate;
    private int seq;
    private String itemName;
    private String customerCd;
    private String customerName;
    private String managerId;                /*출고요청자*/
    private String managerName;
    private String descStorageCd;
    private String descStorageName;
    private String deliveryManagerName;  /*납품담당자*/
    private String deliveryTelno;       /*납품지연락처*/
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

    private BigDecimal totQty;
    private String userId;
}
