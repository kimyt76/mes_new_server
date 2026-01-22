package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class ProcCommonVo {

    private BigInteger workerId;
    private String workerName;

    private BigInteger equipmentId;
    private String equipmentCd;
    private String equipmentName;

    private String etc;

    private String userId;

}
