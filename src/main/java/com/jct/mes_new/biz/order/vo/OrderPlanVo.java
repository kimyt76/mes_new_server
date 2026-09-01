package com.jct.mes_new.biz.order.vo;

import lombok.Data;

@Data
public class OrderPlanVo {

    private Long contractId;
    private Long contractItemId;

    private String rowId;
    private String poNo;
    private String prodType;
    private String clientName;
    private String managerId;
    private String itemCd;
    private String itemName;
    private String qty;
    private String deliveryReqDate;
    private String orderTypeName;
    private String advancePayYn;
    private String prodSheet;
    private String m1Yn;
    private String m2Yn;
    private String bsPlanDate;
    private String bsQty;
    private String bjPlanDate;
    private String bjQty;
    private String outPlanDate;
    private String outQty;
    private String weighProdDate;
    private String matProdDate;
    private String packingProdDate;
    private String storageCnt;
    private String shipmentReqDate;

    private String statusType;
    private String readDay;
    private String typeCd;

    private String strDate;
    private String endDate;
    private String endYn;

    private String field;
    private String value;

    private String userId;
}
