package com.jct.mes_new.biz.base.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ClientVo {
    private BigDecimal rowNum;
    private String clientId;
    private String businessNo;
    private String clientName;
    private String clientType;
    private String clientTypeName;
    private String tradeType;
    private String regDate;
    private String responSalesBiz;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String establishDate;
    private String president;
    private String businessType;
    private String businessItem;
    private String telNo;
    private String faxNo;
    private String homepage;
    private String addressId;
    private String managerId;
    private String paymentCondition;
    private String saleManagerId;
    private String saleManagerIdB;
    private String groupCd;
    private String groupName;
    private String groupsCd;
    private String groupsName;
    private String paymentCd;
    private String paymentName;
    private String useYn;

    private List<ClientManagerVo> managerList;
    private List<ClientHistoryVo> historyList;
    private List<ClientAddressVo> addressList;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String strDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String toDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String firstTradeDate;
    private String saleManagerName;
    private String saleManagerNameB;
    private String userId;
}
