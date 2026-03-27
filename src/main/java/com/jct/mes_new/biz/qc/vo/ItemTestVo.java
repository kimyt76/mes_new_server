package com.jct.mes_new.biz.qc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ItemTestVo {

    private Long testId;
    private String testNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String areaCd;
    private String itemTypeCd;
    private Integer seq;
    private String itemCd;
    private String itemName;
    private String lotNo;
    private String makeNo;
    private BigDecimal qty;
    private String customerCd;
    private String customerName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate shelfLife;
    private String testState;
    private String passState;
    private String passStateName;
    private String endYn;

    private String userId;
}
