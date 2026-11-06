package com.jct.mes_new.biz.purchase.service.impl;

import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.PurchaseOrderMapper;
import com.jct.mes_new.biz.purchase.service.PurchaseOrderService;
import com.jct.mes_new.biz.purchase.service.PurchaseService;
import com.jct.mes_new.biz.purchase.service.TranService;
import com.jct.mes_new.biz.purchase.vo.*;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.service.ItemTestService;
import com.jct.mes_new.biz.qc.service.QcTestService;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseServiceImpl implements PurchaseService {
    private final PurchaseMapper purchaseMapper;
    private final TranService tranService;
    private final ItemTestService itemTestService;
    private final QcTestService qcTestService;
    private final PurchaseOrderService purchaseOrderService;
    private final ItemTestMapper itemTestMapper;

    private final PurchaseOrderMapper purchaseOrderMapper;

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

        // 자동종결 처리 대상 발주서 ID
        Set<Long> purOrderIds = new HashSet<>();

        // 2. 구매 품목 testNo 생성 및 공통값 세팅
        List<PurchaseVo.PurchaseListVo> purchaseItemList = vo.getPurchaseItemList();
        if (purchaseItemList != null && !purchaseItemList.isEmpty()) {
            Map<String, Integer> seqMap = new HashMap<>();

            for (PurchaseVo.PurchaseListVo item : purchaseItemList) {
                String prefix = CodeUtil.createTestNo(
                        purchaseInfo.getPurDate(),
                        purchaseInfo.getAreaCd(),
                        item.getItemTypeCd()
                );

                Integer nextSeq;
                if (seqMap.containsKey(prefix)) {
                    nextSeq = seqMap.get(prefix) + 1;
                } else {
                    nextSeq = itemTestService.getNextTestNoSeq(prefix);
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

                // 발주서 품목 입고완료 여부 확인
                if (item.getPurOrderId() != null && item.getPurOrderId() > 0) {
                    purOrderIds.add(item.getPurOrderId());
                }

            }
        }

        // 4. 원장 저장
        TranRequestVo tranRequestVo = makeTranRequestVo(vo, purId, userId);
        Long tranId = tranService.saveTranInfo(tranRequestVo);

        if (tranId == null) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //5 .Qc품질검사 등록
        List<QcTestVo> qcTestList = makeQcTest(vo,  userId);

        if ( qcTestService.insertQcTest(qcTestList, tranId, userId) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //6. 시험내역 등록
        List<ItemTestVo> itemTestNoList = makeItemTestNo(vo,  userId);

        if ( itemTestService.insertItemTestNo(itemTestNoList, tranId, userId) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        // 7. 발주서가 연결된 경우에만 입고상태 및 자동종결 처리
        if (!purOrderIds.isEmpty()) {
            for (Long purOrderId : purOrderIds) {
                this.updatePurchaseOrderComplete(purOrderId, userId);
            }
        }

        return purId;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updatePurchaseOrderComplete(Long purOrderId, String userId) {
        if (purOrderId == null || purOrderId <= 0) {
            return;
        }

        // 1. 발주서에 속한 모든 발주품목 조회
        List<Long> purOrderItemIds = purchaseOrderMapper.getPurOrderItemIds(purOrderId);

        // 2. 발주품목별 누적 입고수량 계산
        for (Long purOrderItemId : purOrderItemIds) {
            BigDecimal orderQty = purchaseMapper.getPurchaseItemInfo(purOrderItemId);
            BigDecimal inQty = purchaseMapper.getTotalInQty(purOrderItemId);

            if (orderQty == null) {
                continue;
            }

            if (inQty == null) {
                inQty = BigDecimal.ZERO;
            }

            String inYn = inQty.compareTo(orderQty) >= 0 ? "Y" : "N";

            // 발주품목 입고완료 여부 갱신
            purchaseOrderMapper.updateInYn(purOrderItemId, inYn, userId);
        }

        // 3. 모든 발주품목 완료 여부 확인 후 발주마스터 갱신
        purchaseOrderMapper.updatePurchaseOrderEndYn(purOrderId, userId);
    }

    /**
     * 구매 수정
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String updatePurchaseInfo(PurchaseRequestVo vo) {

        if (vo == null || vo.getPurchaseInfo() == null
                || vo.getPurchaseInfo().getPurId() == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER);
        }

        String userId = UserUtil.getUserId();
        PurchaseVo purchaseInfo = vo.getPurchaseInfo();
        Long purId = purchaseInfo.getPurId();

        List<PurchaseVo.PurchaseListVo> itemList = vo.getPurchaseItemList();

        // 자동종결 대상 발주서 ID (중복 제거)
        Set<Long> purOrderIds = new HashSet<>();

        // =====================================================
        // 1. 구매 마스터 수정
        // =====================================================
        purchaseInfo.setUserId(userId);

        if (purchaseMapper.updatePurchaseMst(purchaseInfo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        // =====================================================
        // 2. 삭제 처리
        // =====================================================
        List<String> deleteTestNos = vo.getDeleteTestNos();

        if (deleteTestNos != null && !deleteTestNos.isEmpty()) {
            for (String testNo : deleteTestNos) {
                // 삭제 전 기존 발주서 ID 확보
                Long oldPurOrderId = purchaseMapper.getPurOrderIdByTestNo(testNo);

                if (oldPurOrderId != null && oldPurOrderId > 0) {
                    purOrderIds.add(oldPurOrderId);
                }
                // 구매품목 삭제
                purchaseMapper.deletePurTestNo(testNo);
                // 재고 원장 품목 삭제
                purchaseMapper.deleteInvTranTestNo(testNo);
                // 시험번호 종료 처리
                itemTestMapper.updateItemTestNoEndYn(testNo, userId);
            }
        }
        // =====================================================
        // 3. 구매품목 신규 등록 및 수정
        // =====================================================
        if (itemList != null && !itemList.isEmpty()) {
            Map<String, Integer> seqMap = new HashMap<>();

            for (PurchaseVo.PurchaseListVo item : itemList) {
                item.setPurId(purId);
                item.setUserId(userId);
                // -------------------------------------------------
                // 3-1. 기존 구매품목의 발주서 ID 확보
                // -------------------------------------------------
                if (item.getPurItemId() != null) {
                    Long oldPurOrderId = purchaseMapper.getPurOrderIdByPurItemId(item.getPurItemId());

                    if (oldPurOrderId != null && oldPurOrderId > 0) {
                        purOrderIds.add(oldPurOrderId);
                    }
                }
                // -------------------------------------------------
                // 3-2. 신규 등록
                // -------------------------------------------------
                if (item.getPurItemId() == null) {
                    String prefix = CodeUtil.createTestNo(
                            purchaseInfo.getPurDate(),
                            purchaseInfo.getAreaCd(),
                            item.getItemTypeCd()
                    );

                    Integer nextSeq;

                    if (seqMap.containsKey(prefix)) {
                        nextSeq = seqMap.get(prefix) + 1;
                    } else {
                        nextSeq = itemTestService.getNextTestNoSeq(prefix);
                    }

                    seqMap.put(prefix, nextSeq);
                    String testNo = prefix + String.format("%03d", nextSeq);
                    item.setTestNo(testNo);

                    int insertCnt = purchaseMapper.savePurchaseItemList(item);

                    if (insertCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    // -------------------------------------------------
                    // 3-3. 기존 구매품목 수정
                    // -------------------------------------------------
                    int updateCnt = purchaseMapper.updatePurchaseItemList(item);

                    if (updateCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
                    }
                }

                // -------------------------------------------------
                // 3-4. 신규 또는 수정 후 발주서 ID 수집
                // -------------------------------------------------
                Long purOrderId = item.getPurOrderId();

                if (purOrderId != null && purOrderId > 0) {
                    purOrderIds.add(purOrderId);
                }
            }
            // =====================================================
            // 4. 원장 수정
            // =====================================================
            TranRequestVo tranRequestVo = makeTranRequestVo(vo, purId, userId);
            tranService.updateTranInfo(tranRequestVo);
            // =====================================================
            // 5. QC 품질검사 수정 및 등록
            // =====================================================
            List<QcTestVo> qcTestList = makeQcTest(vo, userId);

            if (qcTestService.updateQcTest(qcTestList, userId) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
            // =====================================================
            // 6. 시험내역 수정 및 등록
            // =====================================================
            List<ItemTestVo> itemTestNoList = makeItemTestNo(vo, userId);

            if (itemTestService.updateItemTestNo(
                    itemTestNoList, userId) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        }
        // =====================================================
        // 7. 발주품목 입고여부 및 발주마스터 자동종결
        // =====================================================
        // 삭제만 발생한 경우에도 실행되도록
        // itemList 조건문 바깥에서 처리
        for (Long purOrderId : purOrderIds) {
            this.updatePurchaseOrderComplete(purOrderId, userId);
        }

        return "수정되었습니다.";
    }

    /**
     *구매 삭제
     * @param purId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public String deletePurchase(Long purId) {
        String userId = UserUtil.getUserId();

        // 구매품목 조회
        List<PurchaseVo.PurchaseListVo> purItemList = purchaseMapper.getPurchaseItemList(purId);
        // 자동종결 대상 발주서 ID
        Set<Long> purOrderIds = new HashSet<>();
        // =====================================================
        // 1. 삭제 전 발주서 ID 수집 및 시험번호 종료
        // =====================================================
        if (purItemList != null && !purItemList.isEmpty()) {
            for (PurchaseVo.PurchaseListVo item : purItemList) {
                String testNo = item.getTestNo();
                // 시험번호 종료
                itemTestMapper.updateItemTestNoEndYn(testNo, userId);

                // 연결된 발주서 ID 수집
                Long purOrderId = item.getPurOrderId();

                if (purOrderId != null && purOrderId > 0) {
                    purOrderIds.add(purOrderId);
                }
            }
        }
        // =====================================================
        // 2. 재고 원장 삭제
        // =====================================================
        purchaseMapper.deleteTranItemList(purId);
        purchaseMapper.deleteTranMst(purId);
        // =====================================================
        // 3. 구매품목 및 구매마스터 삭제
        // =====================================================
        purchaseMapper.deleteItemList(purId);
        purchaseMapper.deletePurMst(purId);
        // =====================================================
        // 4. 발주품목 입고여부 및 발주마스터 자동종결 재계산
        // =====================================================
        for (Long purOrderId : purOrderIds) {
            this.updatePurchaseOrderComplete(purOrderId, userId);
        }

        return "삭제되었습니다.";
    }

    /**
     * 원장 내욕 셋팅
     * @param vo
     * @param purId
     * @param userId
     * @return
     */
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
        tranInfo.setSrcStorageCd(purchaseInfo.getStorageCd());
        tranInfo.setRemark(purchaseInfo.getRemark());
        tranInfo.setEndYn(purchaseInfo.getEndYn());
        tranInfo.setTranTypeCd("A");
        tranInfo.setTranStatus("C");
        tranInfo.setUserId(userId);

        tranRequestVo.setTranInfo(tranInfo);

        // 2. 원장 품목
        List<TranItemVo> tranItemList = new ArrayList<>();

        if (vo.getPurchaseItemList() != null && !vo.getPurchaseItemList().isEmpty()) {
            for (PurchaseVo.PurchaseListVo purchaseItem : vo.getPurchaseItemList()) {
                TranItemVo tranItem = new TranItemVo();

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

    /**
     * 시험번호별 내욕 등록
     * @param vo
     * @param userId
     * @return
     */
    private List<ItemTestVo> makeItemTestNo(PurchaseRequestVo vo,  String userId) {
        // 2. 시험항목 품목
        PurchaseVo mst = vo.getPurchaseInfo();
        List<ItemTestVo> itemTestNoList = new ArrayList<>();


        if (vo.getPurchaseItemList() != null && !vo.getPurchaseItemList().isEmpty()) {
            for (PurchaseVo.PurchaseListVo purchaseItem : vo.getPurchaseItemList()) {
                ItemTestVo itemTest = new ItemTestVo();

                itemTest.setTestNo(purchaseItem.getTestNo());
                itemTest.setCreateDate(mst.getPurDate());
                itemTest.setAreaCd(mst.getAreaCd());
                itemTest.setSeq(1);
                itemTest.setItemTypeCd(purchaseItem.getItemTypeCd());
                itemTest.setItemCd(purchaseItem.getItemCd());
                itemTest.setCustomerCd(mst.getCustomerCd());
                itemTest.setQty(purchaseItem.getQty());
                itemTest.setLotNo(purchaseItem.getLotNo());
                itemTest.setMakeNo("");
                LocalDate shelfLife = LocalDate.now().plusDays(365 * 2);
                LocalDate expiryDate = LocalDate.now().plusDays(365 * 2);

                itemTest.setExpiryDate(
                        purchaseItem.getExpiryDate() != null
                                ? purchaseItem.getExpiryDate()
                                : LocalDate.now().plusYears(2)
                );
                itemTest.setShelfLife(
                        purchaseItem.getExpiryDate() != null
                                ? purchaseItem.getExpiryDate()
                                : LocalDate.now().plusYears(2)
                );
                itemTest.setPassState("REQ");
                itemTest.setTestState("REQ");
                itemTest.setUserId(userId);

                itemTestNoList.add(itemTest);
            }
        }
        return itemTestNoList;
    }

    /**
     *  QC 품질검사
     * @param vo
     * @param userId
     * @return
     */
    private List<QcTestVo> makeQcTest(PurchaseRequestVo vo, String userId) {
        // 2. 시험항목 품목
        PurchaseVo mst = vo.getPurchaseInfo();
        List<QcTestVo> qcTestList = new ArrayList<>();

        if (vo.getPurchaseItemList() != null && !vo.getPurchaseItemList().isEmpty()) {
            for (PurchaseVo.PurchaseListVo purchaseItem : vo.getPurchaseItemList()) {
                QcTestVo qcTest = new QcTestVo();

                qcTest.setRetestYn("N");
                qcTest.setTestNo(purchaseItem.getTestNo());
                qcTest.setReqDate(mst.getPurDate());
                qcTest.setAreaCd(mst.getAreaCd());
                qcTest.setStorageCd(mst.getStorageCd());
                qcTest.setReqTesterId(mst.getManagerId());
                qcTest.setReqQty(purchaseItem.getQty());
                qcTest.setPassState("REQ");
                qcTest.setTestState("REQ");
                qcTest.setUserId(userId);

                qcTestList.add(qcTest);
            }
        }
        return qcTestList;
    }


}
