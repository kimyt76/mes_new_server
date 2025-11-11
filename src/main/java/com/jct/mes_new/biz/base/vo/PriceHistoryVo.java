package com.jct.mes_new.biz.base.vo;


import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class PriceHistoryVo {
    private BigInteger id;
    private String itemCd;
    private String priceType;
    private BigDecimal price;
    private String historyDate;
}
