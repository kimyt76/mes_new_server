package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QcTestTypeVo {

    private Long testTypeMethodId;
    private Long qcTestId;
    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private String itemTypeName ;
    private String testItem;
    private String testItemJoin;
    private String testMethod;
    private String testResult;
    private String testSpec;
    private BigInteger distOrder;

    private String testerName;
    private String passState;
    private String testDateString;

    private String testItemSummary;
    private String regYn;


    private String userId;
}
