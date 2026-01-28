package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ContractVo {

    private String contractId;  /**/
    private String contractDate;  /**/
    private int seq;  /**/
    private String itemCd;  /**/
    private String itemName;  /**/
    private String contractDateSeq;  /**/
    private String clientId;  /**/
    private String clientName;  /**/
    private String managerId;  /**/
    private String managerName;
    private String paymentCondition;  /*결재조건*/
    private String orderType;   /*수주유형 신규, 재발주, 리뉴얼*/
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String expectedDueDate;     /* 납기예정일*/

    private String vatType; /*거래유형   과세, 비과세*/
    private String attachFileId;

    private List<ContractItemVo> itemList;

    private String poNo;
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal vatPrice;
    private BigDecimal supplyPrice;
    private String statusType;
    private String prodType;

    private String contractItemId;
    private String unit;
    private String spec;

    private String strDate;
    private String toDate;

    private String userId;
}
