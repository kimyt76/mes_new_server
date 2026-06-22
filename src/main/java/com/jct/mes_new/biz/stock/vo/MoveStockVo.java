package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class MoveStockVo {

    private Long moveStockId;
    private Long moveReqItemId;
    private Long tranId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveReqDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveRegDate;

    private int regSeq;
    private int seq;

    private String typeCd;
    private String areaCd;
    private String srcStorageCd;
    private String tarStorageCd;
    private String managerId;
    private String moveManagerId;
    private String confirmerId;
    private String moveStatus;
    private String etc;
    private String memo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String itemCd;
    private String itemName;
    private BigDecimal qty;
    private String srcStorageName;
    private String tarStorageName;
    private String managerName;
    private String moveStatusName;
    private String moveManagerName;
    private String confirmerName;

    private String userId;
}
