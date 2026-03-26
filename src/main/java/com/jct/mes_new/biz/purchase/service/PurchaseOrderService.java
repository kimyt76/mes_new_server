package com.jct.mes_new.biz.purchase.service;

import com.jct.mes_new.biz.common.vo.MailVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;

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
     * @param purOrderIds, String itemTypeCd
     */
    void updatePrintYn(List<Long> purOrderIds, String itemTypeCd);

    /**
     * 발주(구매에서 조회 용)
     * @param purOrderIds
     * @return
     */
    List<PurchaseOrderVo.PurchaseOrderListVo> getSubItemList(List<Long> purOrderIds);

    PurchaseOrderRequestVo getPurchaseOrderInfoMail(Map<String, Object> map);

    List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderDetailList(PurchaseOrderVo vo);


    /**
     * 구매등록 후 발주완료 처리
     * @param purOrderId
     * @param userId
     * @return
     */
    void updateEndYn(Long purOrderId, String userId);

    int updateInYn(Long purOrderItemId, String userId);



}

