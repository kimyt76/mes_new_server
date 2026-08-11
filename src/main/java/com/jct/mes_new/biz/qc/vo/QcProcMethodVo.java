package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

@Data
public class QcProcMethodVo {

    private Long  qcProcTestMstId;
    private Long qcProcTestMethodId;

    private String testType;
    private Integer orderDist;
    private String testItem;
    private String testMethod;
    private String testResult;
    private String testerId;

    private String userId;

}

