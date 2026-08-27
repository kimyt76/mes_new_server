package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;

@Data
public class ProdVo {
    private String year;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String prodLgName;
    private String prodMdName;
    private String prodMdCode;

    private Integer mon1Qty;
    private Integer mon2Qty;
    private Integer mon3Qty;
    private Integer mon4Qty;
    private Integer mon5Qty;
    private Integer mon6Qty;
    private Integer mon7Qty;
    private Integer mon8Qty;
    private Integer mon9Qty;
    private Integer mon10Qty;
    private Integer mon11Qty;
    private Integer mon12Qty;
    private Integer totProdQty;

    private Integer mon1Avg;
    private Integer mon2Avg;
    private Integer mon3Avg;
    private Integer mon4Avg;
    private Integer mon5Avg;
    private Integer mon6Avg;
    private Integer mon7Avg;
    private Integer mon8Avg;
    private Integer mon9Avg;
    private Integer mon10Avg;
    private Integer mon11Avg;
    private Integer mon12Avg;
    private Integer totAvgYield;

    private String clientName;

    private BigInteger qty1;
    private BigInteger qty2;
    private BigInteger qty3;
    private BigInteger qty4;
    private BigInteger qty5;
    private BigInteger qty6;
    private BigInteger qty7;
    private BigInteger qty8;
    private BigInteger qty9;
    private BigInteger qty10;
    private BigInteger qty11;
    private BigInteger etcQty;
    private BigInteger totalQty;


}
