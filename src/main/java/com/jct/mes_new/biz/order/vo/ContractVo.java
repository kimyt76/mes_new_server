package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ContractVo {

    private Long contractId;  /**/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate contractDate;  /**/
    private int seq;  /**/
    private Long clientId;  /**/
    private String clientName;  /**/
    private String clientNo;  /**/
    private String managerId;  /**/
    private String managerName;
    private String vatType; /*거래유형   과세, 비과세*/
    private String attachFileId;
    private String setYn;


    /**************** 죄회 부분******************************************************************/
    private String poNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryReqDate;     /* 납기요정일*/
    private String itemCd;  /**/
    private String itemName;  /**/
    private String contractDateSeq;  /**/
    private String orderType;   /*수주유형 신규, 재발주, 리뉴얼*/
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal vatPrice;
    private BigDecimal supplyPrice;
    private BigDecimal outQty;
    private String statusType;
    private String statusTypeName;
    private String prodType;

    private String etc;

    private String userId;
}
