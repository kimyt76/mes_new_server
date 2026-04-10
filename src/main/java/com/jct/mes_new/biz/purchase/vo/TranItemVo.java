package com.jct.mes_new.biz.purchase.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TranItemVo {

    private Long tranId;
    private Long tranItemId;
    private Long purItemId;
    private Long weighInvId;
    private Long weighInvEtcId;
    private String itemTypeCd;
    private String itemCd;
    private String itemName;
    private String spec;
    private String unit;
    private BigDecimal qty;
    private BigDecimal inPrice;
    private BigDecimal supplyPrice;
    private BigDecimal vatPrice;
    private String lotNo;
    private String testNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String inYn;
    private String qcStatus;
    private String etc;


    private String userId;
}
