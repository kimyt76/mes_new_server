package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RealStockVo {

    private Long realStockMstId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate realStockDate;

    private String itemTypeCd;
    private String areaCd;
    private String storageCd;
    private String managerId;
    private Long tranId;
    private String endYn;

    private String itemCd;
    private String itemName;
    private String itemTypeName;
    private BigDecimal docStockQty;
    private BigDecimal reqlStockQty;
    private BigDecimal diffStockQty;
    private String areaName;
    private String storageName;
    private String managerName;

    private String userId;

}
