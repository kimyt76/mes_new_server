package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
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

}
