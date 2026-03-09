package com.jct.mes_new.biz.purchase.vo;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestVo {
    private PurchaseVo purchaseInfo;
    private List<PurchaseVo.PurchaseListVo> purchaseItemList;
}
