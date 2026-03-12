package com.jct.mes_new.biz.purchase.service.impl;

import com.jct.mes_new.biz.common.vo.MailVo;
import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.PurchaseOrderMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseOrderService;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderMapper purchaseOrderMapper;

    public List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderList(PurchaseOrderVo vo){
        if( "M1".equals(vo.getItemTypeCd())) {
            return purchaseOrderMapper.getPurchaseOrderListM1(vo);
        }else{
            return purchaseOrderMapper.getPurchaseOrderListM2(vo);
        }
    }

    //발주서 상세 조회
    public PurchaseOrderRequestVo getPurchaseOrderInfo(Map<String, Object> map) {
        PurchaseOrderRequestVo vo = new PurchaseOrderRequestVo();
        String itemTypeCd = (String) map.get("itemTypeCd");
        Long purOrderId = map.get("purOrderId") == null ? null : ((Number) map.get("purOrderId")).longValue();
        Long purOrderItemId = map.get("purOrderItemId") == null ? null : ((Number) map.get("purOrderItemId")).longValue();

        vo.setPurchaseOrderInfo(purchaseOrderMapper.getPurchaseOrderInfo(purOrderId));

        if ( "M1".equals(itemTypeCd)) {
            vo.setPurchaseOrderItemList(purchaseOrderMapper.getPurchaseOrderItemList(purOrderItemId, itemTypeCd));
        }else{
            vo.setPurchaseOrderItemList(purchaseOrderMapper.getPurchaseOrderItemList(purOrderId, itemTypeCd));
        }

        return vo;
    }

    @Transactional
    public String savePurchaseOrder(PurchaseOrderRequestVo vo){
        String msg = "저장되었습니다.";
        String userId = UserUtil.getUserId();
        PurchaseOrderVo mst = vo.getPurchaseOrderInfo();
        mst.setUserId(userId);

        //mst  저장
        Long cnt = purchaseOrderMapper.insertPurOrderMst(mst);
        if ( cnt <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        for (PurchaseOrderVo.PurchaseOrderItemVo d : vo.getPurchaseOrderItemList()) {
            d.setPurOrderId(mst.getPurOrderId());
        }
        // 3. 품목리스트 저장
        if (!vo.getPurchaseOrderItemList().isEmpty()) {
            if ( purchaseOrderMapper.insertPurOrderItem(vo.getPurchaseOrderItemList(), userId) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return msg;
    }
    
    @Transactional
    public String updatePurchaseOrder(PurchaseOrderRequestVo vo){
        String msg = "수정되었습니다.";
        String userId = UserUtil.getUserId();

        PurchaseOrderVo purchaseOrder = vo.getPurchaseOrderInfo();
        purchaseOrder.setUserId(userId);

        if ( purchaseOrderMapper.updatePurOrderMst(purchaseOrder) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        Long purOrderId = purchaseOrder.getPurOrderId();

        // 2. 삭제 처리
        List<Long> deletedItemIds = vo.getDeletePurchaseOrderItems();
        if (deletedItemIds != null && !deletedItemIds.isEmpty()) {
            purchaseOrderMapper.deleteItemList(purOrderId,deletedItemIds);
        }

        //3. 발주 품목 처리
        List<PurchaseOrderVo.PurchaseOrderItemVo> itemList = vo.getPurchaseOrderItemList();

        if (itemList != null && !itemList.isEmpty()) {
            for (PurchaseOrderVo.PurchaseOrderItemVo item : itemList) {
                item.setPurOrderId(purOrderId);
                item.setUserId(userId);
                if (item.getPurOrderItemId() == null) {
                    // 신규 등록
                    int insertCnt = purchaseOrderMapper.insertPurchaseOrderItem(item);
                    if (insertCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    // 기존 수정
                    int updateCnt = purchaseOrderMapper.updatePurOrderItem(item);
                    if (updateCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }
        }
        return msg;
    }




    /**
     *  이건 메일용
     */
    public List<PurchaseOrderVo> getPurchaseOrderList(String id) {
            return purchaseOrderMapper.getPurchaseOrderList(id);
    }
    public Map<String, Object> getPurchaseOrderMailInfo(String itemTypeCd, Long purOrderId){
        return purchaseOrderMapper.getPurchaseOrderMailInfo(itemTypeCd, purOrderId);
    }
    public void updateMailYn(Map<String, Object> map){
        Long purOrderId = Long.valueOf(map.get("purOrderId").toString());
        purchaseOrderMapper.updateMailYn(purOrderId);
    }

    /**
     *  이건 인쇄용
     */
    public PurchaseOrderRequestVo getPurchaseOrderInfoPrint(Long purOrderId, String itemTypeCd){
        PurchaseOrderRequestVo vo = new PurchaseOrderRequestVo();

        vo.setPurchaseOrderInfo(purchaseOrderMapper.getPurchaseOrderInfo(purOrderId));
        vo.setPurchaseOrderItemList(purchaseOrderMapper.getPurchaseOrderItemList(purOrderId, itemTypeCd));
        return vo;
    }

    public void updatePrintYn(List<Long> purOrderIds, String itemTypeCd){
        purchaseOrderMapper.updatePrintYn(purOrderIds, itemTypeCd);
    }

    /**
     *
     */
    public List<PurchaseOrderVo.PurchaseOrderListVo> getSubItemList(List<Long> purOrderIds){
        return purchaseOrderMapper.getSubItemList(purOrderIds);
    }

}
