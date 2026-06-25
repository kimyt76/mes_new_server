package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
public class UseByVo {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate stdDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String period;

    private String areaCd;
    private String itemCd;
    private String itemName;

    private String testNo;
    private String customerCd;
    private String customerName;
    private int remainingDay ;
    private BigDecimal qty;
    private BigDecimal inPrice;
    private BigDecimal totPrice;
    private BigDecimal totQty;
    private Map<String, BigDecimal> storageQtyMap;
    private String storageCd;
    private String storageName;

}
