package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class ClientApprovalVo {
    private Long clientApprovalId;
    private Long clientId;
    private String approvalOption;
    private BigDecimal firstAmt;
    private BigDecimal middleAmt;
    private BigDecimal lastAmt;
    private int credit;
    private int creditPeriod;
    private String paymentMethod;
    private int orderDist;

    private String userId;



}
