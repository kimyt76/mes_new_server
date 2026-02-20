package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ProcTestTypeMethodVo {
    private BigInteger procTestTypeMethodId;
    private String testType;
    private String testMethod;
    private String testItem;
    private String testTiming;
    private String testTime;
    private String distOrder;
    private String userId;
}
