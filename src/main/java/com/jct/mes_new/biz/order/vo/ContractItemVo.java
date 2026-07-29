package com.jct.mes_new.biz.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractItemVo {
    private Long contractItemId;
    private Long contractId;
    private String poNo;
    private String itemCd;
    private String itemName;
    private String spec;
    private String prodType;    /* 제품유형*/
    private String orderType;    /* 수주유형*/
    private BigDecimal qty;
    private BigDecimal unitPrice;
    private BigDecimal supplyPrice;
    private BigDecimal vatPrice;
    private BigDecimal totPrice;
    private Integer  degree;        /*수주차수*/
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deliveryReqDate;     /* 납기예정일*/
    private String statusType;
    private String etc;

    private Integer orderDist;

    private String userId;
}
