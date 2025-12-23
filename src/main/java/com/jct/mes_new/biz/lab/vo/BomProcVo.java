package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class BomProcVo {

    private String bomProcId;
    private String bomId;
    private String orderDist;
    private String procGb;
    private String phase;
    private String procType;
    private String procTypeName;
    private String matType;
    private String matTypeName;
    private String matProc;
    private BigInteger ho;
    private BigInteger pd;
    private BigInteger d1;
    private BigInteger d2;
    private BigInteger t;
    private BigInteger m;
    private String etc;
    private String useYn;

    private String userId;
}
