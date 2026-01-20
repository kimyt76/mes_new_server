package com.jct.mes_new.biz.purchase.service;

import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;

import java.util.List;

public interface PurchaseOrderService {

    List<PurchaseOrderVo> getPurchaseOrderList(String id);
}
