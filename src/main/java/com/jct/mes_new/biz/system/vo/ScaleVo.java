package com.jct.mes_new.biz.system.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
public class ScaleVo {

    private BigInteger scaleId;
    private String scaleCd;
    private String scaleName;
    private String scaleType;
    private String scaleNickname;
    private String modelName;
    private String storageCd;
    private String nportAddress;
    private Integer serialPortNo;
    private Integer responsePortNo;
    private String equipNo;
    private String etc;
    private Date lastTime;
    private BigDecimal lastValue;

    private String areaCd;
    private String areaName;
    private String storageName;
    private String useYn;

    private String userId;
}
