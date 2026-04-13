package com.jct.mes_new.biz.purchase.mapper;

import com.jct.mes_new.biz.purchase.vo.PurchaseOrderVo;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseMapper {

    /**
     * 구매조회
     * @param vo
     * @return
     */
    List<PurchaseVo.searchPurchaseListVo> getPurchaseList(PurchaseVo vo);

    /**
     * 구매상세조회
     * @param purId
     * @return
     */
    PurchaseVo getPurchaseInfo(Long purId);
    List<PurchaseVo.PurchaseListVo> getPurchaseItemList(Long purId);

    /**
     * 구매 저장
     * @param purchaseInfo
     * @return
     */
    int savePurchaseMst(PurchaseVo purchaseInfo);
    int savePurchaseItemList(PurchaseVo.PurchaseListVo item);

    /**
     * 구매 수정
     * @param purchaseInfo
     * @return
     */
    int updatePurchaseMst(PurchaseVo purchaseInfo);
    void deletePurchaseItemList(Long purId);
    int updatePurchaseItemList(PurchaseVo.PurchaseListVo item);

    /**
     * 구매 삭제
     * @param purId
     */
    void deletePurMst(Long purId);
    void deleteItemList(Long purId, List<Long> deletedItemIds);

    /**
     * 구매 현황 조회
     * @param vo
     * @return
     */
    List<PurchaseVo.searchPurchaseListVo> getPurchaseDetailList(PurchaseVo vo);



}
