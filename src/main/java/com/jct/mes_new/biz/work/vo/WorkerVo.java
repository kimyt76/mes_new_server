package com.jct.mes_new.biz.work.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class WorkerVo {

    private BigInteger workerId;
    private String workerName;
    private String areaCd;
    private String areaName;
    private String processCd;
    private String processName;
    private String useYn;
    private String etc;

    private String userId;
}
