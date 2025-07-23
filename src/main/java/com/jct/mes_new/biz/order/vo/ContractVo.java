package com.jct.mes_new.biz.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ContractVo {

    private String contractId;
    private String contractDate;
    private int seq;
    private String itemName;
    private String contractDateSeq;
    private String customerCd;
    private String customerName;
    private String managerId;
    private String managerName;
    private String descStorageCd;
    private String descStorageName;
    private String transactionType;
    private String currencyType;
    private String paymentCondition;
    private String expiryDate;   /*유효기간*/
    private String dueDate;     /* 납기일*/
    private String tradingMethod;
    private String statusType;
    private String attachFileId;
    private String printYn;

    private List<ItemListVo> itemList;

    private BigDecimal totQty;
    private BigDecimal totSupplyPrice;

    private String userId;
}
