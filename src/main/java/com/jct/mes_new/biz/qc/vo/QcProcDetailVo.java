package com.jct.mes_new.biz.qc.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QcProcDetailVo {
    private Long qcProcTestDetailId;
    private Long qcProcTestMstId;
    private String testType;
    private Integer orderDist;
    private String testTime;
    private BigDecimal line1;
    private BigDecimal line2;
    private BigDecimal line3;
    private BigDecimal line4;
    private BigDecimal line5;
    private BigDecimal line6;
    private BigDecimal line7;
    private BigDecimal line8;
    private BigDecimal line9;
    private BigDecimal line10;
    private String passYn;

    private String userId;

}
