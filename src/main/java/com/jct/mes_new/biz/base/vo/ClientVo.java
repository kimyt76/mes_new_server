package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ClientVo {
    private BigDecimal rowNum;
    private Long clientId;
    private String businessNo;
    private String clientName;
    private String clientType;
    private String clientTypeName;
    private String tradeType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate clientRegDate;
    private String responSalesBiz;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate establishDate;
    private String president;
    private String businessType;
    private String businessItem;
    private String telNo;
    private String faxNo;
    private String email;
    private String homepage;
    private String groupCd;
    private String groupName;
    private String groupsCd;
    private String groupsName;
    private String paymentCd;
    private String paymentName;
    private String useYn;
    private String managerRank;
    private String managerRank2;

    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String strDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String endDate;
    private String managerRank2From;
    private String managerRank2To;
    private String businessManagerName;
    private BigDecimal oneYearAgo;
    private BigDecimal twoYearAgo;
    private BigDecimal threeYearAgo;
    private String firstDealDate;
    private String lastDealDate;

    private String userId;
}
