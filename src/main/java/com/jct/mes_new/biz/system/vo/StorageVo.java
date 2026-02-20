package com.jct.mes_new.biz.system.vo;

import lombok.Data;

import java.math.BigInteger;

@Data
public class StorageVo {
    private String storageCd;
    private String storageName;
    private String areaCd;
    private String areaName;
    private BigInteger rackId;

    private String m0Yn;
    private String m1Yn;
    private String m2Yn;
    private String m3Yn;
    private String m5Yn;
    private String m6Yn;
    private String m7Yn;
    private String prodYn;
    private String useYn;

    private String userId;
}
