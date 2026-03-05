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
     * @param itemTypeCd, purOrderId
     * @return
     */
    Map<String, Object> getPurchaseOrderMailInfo(String itemTypeCd, Long purOrderId);

    void updateMailYn(Map<String, Object> map);

    PurchaseOrderRequestVo getPurchaseOrderInfoPrint(Long purOrderId, String itemTypeCd);

    /**
     * print용
     * @param map
     */
    void updatePrintYn(List<Long> purOrderIds, String itemTypeCd);
}

