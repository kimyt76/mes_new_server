package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

@Data
public class QcTestTypeReportVo {
    private Integer rowNo;
    private String testItem;
    private String testMethod;
    private String testSpec;
    private String testResult;
    private String testDateString;
    private String testMember;
    private String confirmMember;
    private String passStateName;
}
