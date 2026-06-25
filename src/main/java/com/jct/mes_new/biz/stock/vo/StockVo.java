package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.poi.hpsf.Decimal;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class StockVo {

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private String itemTypeName;
    private String spec;
    private String areaCd;
    private String storageCd;
    private String storageName;
    private String testNo;
    private String lotNo;
    private String etc;
    private String endYn;
    private String type;

    private BigDecimal qty;
    private BigDecimal stockQty;
    private BigDecimal totQty;
    private BigDecimal inReQty;
    private BigDecimal saftQty;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate stdDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tranDate;


    private Map<String, BigDecimal> storageQtyMap = new LinkedHashMap<>();

}
