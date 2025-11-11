package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleVo {

    private String saleId;
    private String saleDate;
    private int seq;
    private String itemName;
    private String saleDateSeq;
    private String customerCd;
    private String customerName;
    private String managerId;
    private String managerName;
    private String descStorageCd;
    private String descStorageName;
    private String transactionType;
    private String transactionTypeName;
    private String currencyType;
    private String tradingMethod;
    private String statusType;
    private String printYn;

    private List<ContractItemVo> itemList;

    private String contractIds;
    private BigDecimal totPrice;

    private String userId;
}
