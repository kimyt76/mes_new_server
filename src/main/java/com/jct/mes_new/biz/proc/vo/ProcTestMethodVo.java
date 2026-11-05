package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

@Data
public class ProcTestMethodVo {

    private Long procTestTabId;
    private Long procTestId;
    private String testMethod;
    private String testItem;
    private String testTiming;
    private String testTime;
    private String testResult;

    private String userId;

}
