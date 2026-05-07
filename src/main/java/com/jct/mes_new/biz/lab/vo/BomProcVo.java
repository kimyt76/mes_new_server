package com.jct.mes_new.biz.lab.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

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
    private String h;
    private String p;
    private String d1;
    private String d2;
    private String t;
    private String m;
    private String p2;
    private String rt;
    private String etc;
    private String useYn;
    private String bomVer;
    private String significant;

    private String note;

    private String userId;
}
