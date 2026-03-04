package com.jct.mes_new.biz.purchase.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequestVo {

    private PurchaseOrderVo purchaseOrderInfo;
    private List<PurchaseOrderVo.PurchaseOrderItemVo> purchaseOrderItemList;
}
