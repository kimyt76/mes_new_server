package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

@Data
public class ProcTestVo {
    private Long procTestId;
    private Long workProcId;
    private String inspector;
    private String areaCd;
    private String storageCd;
    private String powderTemperature;
    private String powderHumidity;
    private String liquidTemperature;
    private String liquidHumidity;
    private String etc;
    private String procStatus;


    private String procCd;
    private String userId;
}
