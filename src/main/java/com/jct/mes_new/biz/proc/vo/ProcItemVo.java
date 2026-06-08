package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcItemVo {

    private String itemCd;
    private String itemName;
    private BigDecimal qty;
    private BigDecimal prodQty;
    private String etc;
}
