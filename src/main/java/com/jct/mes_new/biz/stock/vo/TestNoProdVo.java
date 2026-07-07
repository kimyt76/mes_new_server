package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TestNoProdVo {
    private Long weightId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate procOrderDate;
    private String testNo;
    private String itemCd;
    private String itemName;
    private String itemCdM1;
    private String itemNameM1;
    private String itemCdM0;
    private String itemNameM0;
    private String lotNo;
    private BigDecimal qty;
}
