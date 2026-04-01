package com.jct.mes_new.biz.qc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class QcTestVo {

    private Long qcTestId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String retestYn;
    private String seq;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate reqDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate testDate;
    private String testNo;
    private String areaCd;
    private String storageCd;
    private String storageName;
    private BigDecimal reqQty;
    private BigDecimal testQty;
    private BigDecimal sampleQty;
    private String orderType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate confirmDate;
    private String testState;
    private String passState;
    private String passStateName;

    private String reqTesterId;
    private String reqTesterName;
    private String testerId;
    private String testerName;
    private String orderTesterId;
    private String orderTesterName;
    private String confirmTesterId;
    private String confirmTesterName;
    private String sampleTesterId;
    private String sampleTesterName;

    private String etc;
    private String qcCd;

    private String itemCd;
    private String itemName;
    private String itemTypeCd;
    private String itemTypeName;
    private String expiryDate;
    private String lotNo;
    private String makeNo;
    private String customerCd;
    private String customerName;

    private String menuType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate createDate;

    private String userId;
}
