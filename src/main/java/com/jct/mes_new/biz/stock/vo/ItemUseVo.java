package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ItemUseVo {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;
    private String makeNo;
    private String itemCd;
    private String itemName;
    private BigDecimal orderQty;
    private String unit;
    private String bomItemCd;
    private String bomItemName;
    private String spec;
    private BigDecimal qty;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String procCd;


}
