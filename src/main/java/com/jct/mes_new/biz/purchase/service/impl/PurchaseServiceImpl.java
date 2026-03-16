package com.jct.mes_new.biz.purchase.service.impl;

import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseService;
import com.jct.mes_new.biz.purchase.service.TranService;
import com.jct.mes_new.biz.purchase.vo.*;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseMapper purchaseMapper;
    private final TranService tranService;

    /**
     * 구매 조회
     * @param vo
     * @return
     */
    public List<PurchaseVo.searchPurchaseListVo> getPurchaseList(PurchaseVo vo){
        return purchaseMapper.getPurchaseList(vo);
    }

    /**
     * 구매 현황 조회
     * @param vo
     * @return
     */
    public List<PurchaseVo.searchPurchaseListVo> getPurchaseDetailList(PurchaseVo vo){
        return purchaseMapper.getPurchaseDetailList(vo);
    }

    /**
     * 구매상세조회
     * @param purId
     * @return
     */
    public PurchaseRequestVo getPurchaseInfo(Long purId){
        PurchaseRequestVo vo = new PurchaseRequestVo();

        vo.setPurchaseInfo(purchaseMapper.getPurchaseInfo(purId));
        vo.setPurchaseItemList(purchaseMapper.getPurchaseItemList(purId));
        return vo;
    }

    /**
     * 구매 저장
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long savePurchaseInfo(PurchaseRequestVo vo) {
        if (vo == null || vo.getPurchaseInfo() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
        }

        String userId = UserUtil.getUserId();
        PurchaseVo purchaseInfo = vo.getPurchaseInfo();
        purchaseInfo.setUserId(userId);

        // 1. 구매 마스터 저장
        if (purchaseMapper.savePurchaseMst(purchaseInfo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        Long purId = purchaseInfo.getPurId();

        // 2. 구매 품목 testNo 생성 및 공통값 세팅
        List<PurchaseVo.PurchaseListVo> purchaseItemList = vo.getPurchaseItemList();
        if (purchaseItemList != null && !purchaseItemList.isEmpty()) {
            Map<String, Integer> seqMap = new HashMap<>();

            for (PurchaseVo.PurchaseListVo item : purchaseItemList) {
                log.info("itemCd={}, itemName={}, itemTypeCd={}",
                        item.getItemCd(), item.getItemName(), item.getItemTypeCd());
                String prefix = CodeUtil.createTestNo(
                        purchaseInfo.getPurDate(),
                        purchaseInfo.getAreaCd(),
                        item.getItemTypeCd()
                );

                Integer nextSeq;
                if (seqMap.containsKey(prefix)) {
                    nextSeq = seqMap.get(prefix) + 1;
                } else {
                    nextSeq = purchaseMapper.getNextTestNoSeq(prefix);
                }
                seqMap.put(prefix, nextSeq);

                String testNo = prefix + String.format("%03d", nextSeq);

                item.setPurId(purId);
                item.setTestNo(testNo);
                item.setUserId(userId);

                // 3. 구매 품목 저장
                if (purchaseMapper.savePurchaseItemList(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }
        // 4. 원장 저장
        TranRequestVo tranRequestVo = makeTranRequestVo(vo, purId, userId);
        Long tranId = tranService.saveTranInfo(tranRequestVo);

        if (tranId == null) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return purId;
    }

    /**
     * 구매 수정
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String updatePurchaseInfo(PurchaseRequestVo vo){
        String msg = "수정되었습니다.";
        String userId = UserUtil.getUserId();
        Long purId = vo.getPurchaseInfo().getPurId();
        //mst
        vo.getPurchaseInfo().setUserId(userId);
        if(purchaseMapper.updatePurchaseMst(vo.getPurchaseInfo()) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        // 2. 삭제 처리
        List<Long> deletedItemIds = vo.getDeletePurchaseItems();
        if (deletedItemIds != null && !deletedItemIds.isEmpty()) {
            purchaseMapper.deleteItemList(purId,deletedItemIds);
        }

        List<PurchaseVo.PurchaseListVo> itemList = vo.getPurchaseItemList();

        if (itemList != null && !itemList.isEmpty()) {
            for (PurchaseVo.PurchaseListVo item : itemList) {
                item.setPurId(purId);
                item.setUserId(userId);

                if (item.getPurItemId() == null) {
                    // 신규 등록
                    int insertCnt = purchaseMapper.savePurchaseItemList(item);
                    if (insertCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    // 기존 수정
                    int updateCnt = purchaseMapper.updatePurchaseItemList(item);
                    if (updateCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }
            }

            //원장 수정
            TranRequestVo tranRequestVo = makeTranRequestVo(vo, purId, userId);
            tranRequestVo.setDeleteTranItems(deletedItemIds);

            tranService.updateTranInfo(tranRequestVo);

        }
        return msg;
    }

    /**
     *구매 삭제
     * @param purId
     * @return
     */
    public String deletePurchase(Long purId){
        String msg="삭제되었습니다.";

        purchaseMapper.deletePurchaseItemList(purId);
        purchaseMapper.deletePurMst(purId);

        tranService.deleteTranInfo(purId);

        return msg;
    }


    private TranRequestVo makeTranRequestVo(PurchaseRequestVo vo, Long purId, String userId) {
        PurchaseVo purchaseInfo = vo.getPurchaseInfo();
        TranRequestVo tranRequestVo = new TranRequestVo();

        // 1. 원장 마스터
        TranVo tranInfo = new TranVo();
        tranInfo.setPurId(purId);
        tranInfo.setTranId(vo.getPurchaseItemList().get(0).getTranId());
        tranInfo.setTranDate(purchaseInfo.getPurDate());
        tranInfo.setCustomerCd(purchaseInfo.getCustomerCd());
        tranInfo.setManagerId(purchaseInfo.getManagerId());
        tranInfo.setAreaCd(purchaseInfo.getAreaCd());
        tranInfo.setToStorageCd(purchaseInfo.getStorageCd());
        tranInfo.setRemark(purchaseInfo.getRemark());
        tranInfo.setEndYn(purchaseInfo.getEndYn());
        tranInfo.setTranTypeCd("A");
        tranInfo.setUserId(userId);

        tranRequestVo.setTranInfo(tranInfo);

        // 2. 원장 품목
        List<TranVo.TranItemListVo> tranItemList = new ArrayList<>();

        if (vo.getPurchaseItemList() != null && !vo.getPurchaseItemList().isEmpty()) {
            for (PurchaseVo.PurchaseListVo purchaseItem : vo.getPurchaseItemList()) {
                TranVo.TranItemListVo tranItem = new TranVo.TranItemListVo();

                tranItem.setItemCd(purchaseItem.getItemCd());
                tranItem.setItemName(purchaseItem.getItemName());
                tranItem.setItemTypeCd(purchaseItem.getItemTypeCd());
                tranItem.setSpec(purchaseItem.getSpec());
                tranItem.setTestNo(purchaseItem.getTestNo());
                tranItem.setExpiryDate(purchaseItem.getExpiryDate());
                tranItem.setEtc(purchaseItem.getEtc());
                tranItem.setQty(purchaseItem.getQty());
                tranItem.setLotNo(purchaseItem.getLotNo());
                tranItem.setInYn(purchaseItem.getInYn());
                tranItem.setInPrice(purchaseItem.getInPrice());
                tranItem.setSupplyPrice(purchaseItem.getSupplyPrice());
                tranItem.setVatPrice(purchaseItem.getVatPrice());
                tranItem.setPurItemId(purchaseItem.getPurItemId());
                tranItem.setTranItemId(purchaseItem.getTranItemId());
                tranItem.setQcStatus(purchaseItem.getQcStatus());
                tranItem.setUserId(userId);

                tranItemList.add(tranItem);
            }
        }

        tranRequestVo.setTranItemList(tranItemList);

        return tranRequestVo;
    }


}
