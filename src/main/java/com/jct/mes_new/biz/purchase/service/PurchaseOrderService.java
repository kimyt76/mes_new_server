package com.jct.mes_new.biz.purchase.service;

import com.jct.mes_new.biz.common.vo.MailVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;

import java.util.List;
import java.util.Map;

public interface PurchaseOrderService {

    List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderList(PurchaseOrderVo vo);

    List<PurchaseOrderVo> getPurchaseOrderList(String id);

    String savePurchaseOrder(PurchaseOrderRequestVo vo);

    PurchaseOrderRequestVo getPurchaseOrderInfo(Map<String, Object> map);

    String updatePurchaseOrder(PurchaseOrderRequestVo vo);

    /**
     * 메일용
     * @param map
     * @return
     */
    Map<String, Object> getPurchaseOrderMailInfo(Map<String, Object> map);
}

