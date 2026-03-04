package com.jct.mes_new.biz.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PurchaseOrderSheet {
    private String memberName;
    private String orderDate;
    private String deliveryDate;
    private String email;
    private String customerName;
    private String customerManager;
    private String tel;
    private String fax;
    private String address;
    private String hanAmount;
    private String numAmount;
    private BigDecimal totOrderQty;
    private BigDecimal totSupplyAmt;
    private BigDecimal totVat;
    private BigDecimal totAmt;

    private String line1;
    private String line2;
    private String line3;
    private String line4;

    private List<PurchaseOrderMailVo> orderItems;
}
