package com.jct.mes_new.biz.purchase.mapper;

import com.jct.mes_new.biz.purchase.vo.PurchaseOrderRequestVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PurchaseOrderMapper {

    List<PurchaseOrderVo> getPurchaseOrderList(@Param("purOrderId") String id);

    List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderListM1(PurchaseOrderVo vo);

    List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderListM2(PurchaseOrderVo vo);

    /**
     * 발주현황
     * @param vo
     * @return
     */
    List<PurchaseOrderVo.PurchaseOrderListVo> getPurchaseOrderDetailList(PurchaseOrderVo vo);

    /**
     * 발주 마스터 등록
     * @param vo
     * @return
     */
    long insertPurOrderMst(PurchaseOrderVo vo);

    /**
     * 발주 item 일괄 등록
     * @param purchaseOrderItemList
     * @return
     */
    int insertPurOrderItem(@Param("list") List<PurchaseOrderVo.PurchaseOrderItemVo> purchaseOrderItemList, @Param("userId") String userId);

    /**
     *  발주 상세
     * @param purOrderId
     * @return
     */
    PurchaseOrderVo getPurchaseOrderInfo(@Param("purOrderId") Long purOrderId);
    List<PurchaseOrderVo.PurchaseOrderItemVo> getPurchaseOrderItemList(@Param("purOrderId") Long purOrderId, @Param("itemTypeCd") String itemTypeCd);

    /**
     * 발주 업데이트
     * @param
     * @return
     */
    void deleteItemList(@Param("purOrderId") Long purOrderId,
                        @Param("deletedItemIds") List<Long> deletedItemIds);
    /* 발주 item 리스트 신규*/
    int insertPurchaseOrderItem(PurchaseOrderVo.PurchaseOrderItemVo vo);
    /* 발주 마스터 업데이트*/
    int updatePurOrderMst(PurchaseOrderVo purchaseOrderInfo);
    /* 발주 item 리스트 업데이트*/
    int updatePurOrderItem(PurchaseOrderVo.PurchaseOrderItemVo item);


    /**
     * 메일룡
     * @param itemTypeCd, purOrderId
     * @return
     */
    Map<String, Object> getPurchaseOrderMailInfo(@Param("itemTypeCd") String itemTypeCd, @Param("purOrderId") Long purOrderId);
    void updateMailYn(@Param("purOrderId") Long purOrderId);

    void updatePrintYn(@Param("purOrderIds") List<Long> purOrderIds, @Param("itemTypeCd") String itemTypeCd);

    List<PurchaseOrderVo.PurchaseOrderListVo> getSubItemList(@Param("purOrderIds") List<Long> purOrderIds);



}
