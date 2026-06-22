package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProdOutVo {
    private Long tranId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate tranDate;
    private int seq;
    private String tranDateSeq;
    private String areaCd;
    private String areaName;
    private String srcStorageCd;
    private String srcStorageName;
    private String tranTypeCd;
    private String managerId;
    private String managerName;
    private String etc;
    private String itemCd;
    private String itemName;
    private String clientId;
    private String clientName;
    private String poNo;
    private BigDecimal qty;


    private String userId;


}
