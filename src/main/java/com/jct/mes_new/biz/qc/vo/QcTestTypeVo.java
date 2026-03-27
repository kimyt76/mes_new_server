package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.math.BigInteger;
import java.time.LocalDate;

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

    private String testNo;
    private String testerName;
    private String testDate;
    private String passState;
    private String testState;
    private String testDateString;

    private LocalDate strDate;
    private LocalDate endDate;
    private String testItemSummary;
    private String regYn;


    private String userId;
}
