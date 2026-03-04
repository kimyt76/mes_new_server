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
     * 원재료 등록
     */
    int insertPurOrderBatch(PurchaseOrderRequestVo vo);

    /**
     * 부재료 마스터 등록
     * @param vo
     * @return
     */
    long insertPurOrderMst(PurchaseOrderVo vo);

    /**
     * 부래죠 item 등록
     * @param purchaseOrderItemList
     * @return
     */
    int insertPurOrderItem(@Param("list") List<PurchaseOrderVo.PurchaseOrderItemVo> purchaseOrderItemList, @Param("userId") String userId);

    /**
     *  발주 상세
     * @param purOrderId
     * @return
     */
    PurchaseOrderVo getPurchaseOrderInfoM1(@Param("purOrderId") Long purOrderId);
    List<PurchaseOrderVo.PurchaseOrderItemVo> getPurchaseOrderItemListM1(@Param("purOrderId") Long purOrderId);
    PurchaseOrderVo getPurchaseOrderInfoM2(@Param("purOrderId") Long purOrderId);
    List<PurchaseOrderVo.PurchaseOrderItemVo> getPurchaseOrderItemListM2(@Param("purOrderId") Long purOrderId);

    /**
     * 발주 업데이트
     * @param
     * @return
     */
    void deleteItemList(Long purOrderId);
    int updatePurOrderMst(PurchaseOrderVo purchaseOrderInfo);
    int updatePurOrderBatch(PurchaseOrderRequestVo vo);

    /**
     * 메일룡
     * @param itemTypeCd, purOrderId
     * @return
     */
    Map<String, Object> getPurchaseOrderMailInfo(@Param("itemTypeCd") String itemTypeCd, @Param("purOrderId") Long purOrderId);
}
