package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class QcTestTypeVo {

    private BigInteger testTypeMethodId;
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

    private String testItemSummary;
    private String regYn;


    private String userId;
}
