package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.excel.*;
import com.jct.mes_new.biz.base.mapper.*;
import com.jct.mes_new.biz.base.service.DailyReportService;
import com.jct.mes_new.biz.base.vo.*;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class DailyReportServiceImpl implements DailyReportService {

    private final DailyReportMapper dailyReportMapper;
    private final M0DailyReportMapper m0DailyReportMapper;
    private final M1DailyReportMapper m1DailyReportMapper;
    private final M2DailyReportMapper m2DailyReportMapper;
    private final LaborCostsMapper laborCostsMapper;
    private final DailyMgmtMapper dailyMgmtMapper;

    private final M1DailyReportExcel m1DailyReportExcel;
    private final M2DailyReportExcel m2DailyReportExcel;
    private final M0DailyReportExcel m0DailyReportExcel;
    private final LaborCostExcel laborCostExcel;
    private final DailyReportMgmtExcel dailyReportMgmtExcel;

    public String updateDailyReportEndYn(DailyReportVo vo) {
        dailyReportMapper.updateDailyReportEndYn(vo);
        return "저장되었습니다.";
    }

    public List<DailyReportVo> getM1DailyReportList(DailyReportVo vo) {
        return dailyReportMapper.getM1DailyReportList(vo);
    }

    public DailyReportRequestVo getM1DailyReportInfo(DailyReportVo vo) {
        DailyReportRequestVo req = new DailyReportRequestVo();
        Long dailyId = vo.getDailyId();
        if (dailyId == null) {
            //입고
            vo.setTranTypeCd("A");
            vo.setItemTypeCd("M1");
            req.setInList(dailyReportMapper.getInList(vo));
            //반품
            vo.setTranTypeCd("P");
            req.setReturnList(dailyReportMapper.getInList(vo));
            //칭량
            req.setProdList(dailyReportMapper.getProdList(vo));
            //원료
            req.setUseList(dailyReportMapper.getUseList(vo));
        } else {
            req.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));
            //입고
            vo.setTranTypeCd("A");
            vo.setItemTypeCd("M1");
            req.setInList(m1DailyReportMapper.getDailyReportList(vo));
            //반품
            vo.setTranTypeCd("P");
            vo.setItemTypeCd("M1");
            req.setReturnList(m1DailyReportMapper.getDailyReportList(vo));
            //불량
            vo.setTranTypeCd("G");
            vo.setItemTypeCd("M1");
            req.setDiscardList(m1DailyReportMapper.getDailyReportList(vo));
            //칭량
            req.setProdList(m1DailyReportMapper.getProdList(vo));
            //원료
            req.setUseList(m1DailyReportMapper.getUseList(vo));
            //외부출고
            vo.setTranTypeCd("Z");
            vo.setItemTypeCd("M1");
            req.setOspList(m1DailyReportMapper.getDailyReportList(vo));
        }

        return req;
    }

    @Transactional
    public String saveDailyReportM1(DailyReportRequestVo vo) {
        String userId = UserUtil.getUserId();

        DailyReportVo mst = vo.getDailyReportInfo();

        List<DailyReportVo> inList = vo.getInList();       // 입고
        List<DailyReportVo> returnList = vo.getReturnList();   // 반품
        List<DailyReportVo> discardList = vo.getDiscardList();  // 불량/폐기
        List<DailyReportVo> prodList = vo.getProdList();    // 제품 사용량
        List<DailyReportVo> useList = vo.getUseList();    // 원료 사용량
        List<DailyReportVo> ospList = vo.getOspList();      // 외부반출

        List<Long> deleteInIds = vo.getDeleteInIds();
        List<Long> deleteReturnIds = vo.getDeleteReturnIds();
        List<Long> deleteDiscardIds = vo.getDeleteDiscardIds();
        List<Long> deleteProdIds = vo.getDeleteProdIds();
        List<Long> deleteUseIds = vo.getDeleteUseIds();
        List<Long> deleteOspIds = vo.getDeleteOspIds();

        /*
         * 1. 마스터 저장
         * dailyId가 없을 때만 최초 1회 INSERT
         */
        if (mst.getDailyId() == null) {
            mst.setUserId(userId);
            if (dailyReportMapper.insertDailyReportMst(mst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        Long dailyId = mst.getDailyId();

        /*
         * 2. 삭제
         * 삭제할 ID가 있을 경우에만 실행
         */
        if (deleteInIds != null && !deleteInIds.isEmpty()) {
            m1DailyReportMapper.deleteDailyReport(deleteInIds);
        }

        if (deleteReturnIds != null && !deleteReturnIds.isEmpty()) {
            m1DailyReportMapper.deleteDailyReport(deleteReturnIds);
        }

        if (deleteDiscardIds != null && !deleteDiscardIds.isEmpty()) {
            m1DailyReportMapper.deleteDailyReport(deleteDiscardIds);
        }

        if (deleteProdIds != null && !deleteProdIds.isEmpty()) {
            m1DailyReportMapper.deleteDailyReportProd(deleteProdIds);
        }

        if (deleteUseIds != null && !deleteUseIds.isEmpty()) {
            m1DailyReportMapper.deleteDailyReportUse(deleteUseIds);
        }

        if (deleteOspIds != null && !deleteOspIds.isEmpty()) {
            m1DailyReportMapper.deleteDailyReport(deleteOspIds);
        }

        /*
         * 3. 입고
         * A : 입고
         */
        saveM1List(inList, dailyId, userId, "A");

        /*
         * 4. 반품
         * P : 반품
         */
        saveM1List(returnList, dailyId, userId, "P");

        /*
         * 5. 불량/폐기
         * G : 불량/폐기
         */
        saveM1List(discardList, dailyId, userId, "G");

        /*
         * 6. 제품 사용량
         * tranTypeCd는 실제 코드에 맞게 변경
         */
        for (DailyReportVo prod : prodList) {
            prod.setDailyId(dailyId);
            prod.setUserId(userId);

            if (prod.getDailyProdId() == null) {
                if (m1DailyReportMapper.insertDailyReportProd(prod) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            } else {
                if (m1DailyReportMapper.updateDailyReportProd(prod) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }

        /*
         * 7. 원료 사용량
         * tranTypeCd는 실제 코드에 맞게 변경
         */
        for (DailyReportVo use : useList) {
            use.setDailyId(dailyId);
            use.setUserId(userId);

            if (use.getDailyItemUseId() == null) {
                if (m1DailyReportMapper.insertDailyReportUse(use) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            } else {
                if (m1DailyReportMapper.updateDailyReportUse(use) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }
        /*
         * 8. 외부반출
         * Z : 외부반출
         */
        saveM1List(ospList, dailyId, userId, "Z");

        return "저장되었습니다.";
    }

    private void saveM1List(List<DailyReportVo> list, Long dailyId, String userId, String tranTypeCd) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DailyReportVo item : list) {
            item.setDailyId(dailyId);
            item.setUserId(userId);
            item.setTranTypeCd(tranTypeCd);
            /*
             * PK가 없으면 신규
             */
            if (item.getDailyItemId() == null) {
                if (m1DailyReportMapper.insertDailyReportM1(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
                /*
                 * PK가 있으면 수정
                 */
            } else {
                if (m1DailyReportMapper.updateDailyReportM1(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
    }

    /********************************** 부자재 ******************************************************************/
    public List<DailyReportVo> getM2DailyReportList(DailyReportVo vo) {
        return dailyReportMapper.getM2DailyReportList(vo);
    }


    public DailyReportRequestVo getM2DailyReportInfo(DailyReportVo vo) {
        DailyReportRequestVo req = new DailyReportRequestVo();
        Long dailyId = vo.getDailyId();

        if (dailyId == null) {
            //입고
            vo.setTranTypeCd("A");
            vo.setItemTypeCd("M2");
            req.setInList(dailyReportMapper.getInList(vo));
            //반품
            vo.setTranTypeCd("P");
            req.setReturnList(dailyReportMapper.getInList(vo));
            //사용량
            req.setUseList(dailyReportMapper.getUseM2List(vo));
        } else {
            req.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));
            vo.setDailyId(dailyId);
            //입고
            vo.setTranTypeCd("A");
            vo.setItemTypeCd("M2");
            req.setInList(m2DailyReportMapper.getDailyReportList(vo));
            //반품
            vo.setTranTypeCd("P");
            vo.setItemTypeCd("M2");
            req.setReturnList(m2DailyReportMapper.getDailyReportList(vo));
            //불량
            vo.setTranTypeCd("G");
            vo.setItemTypeCd("M2");
            req.setDiscardList(m2DailyReportMapper.getDailyReportList(vo));
            //사용량
            req.setUseList(m2DailyReportMapper.getUseList(vo));
        }

        return req;
    }

    @Transactional
    public String saveDailyReportM2(DailyReportRequestVo vo) {
        String userId = UserUtil.getUserId();

        DailyReportVo mst = vo.getDailyReportInfo();

        List<DailyReportVo> inList = vo.getInList();       // 입고
        List<DailyReportVo> returnList = vo.getReturnList();   // 반품
        List<DailyReportVo> discardList = vo.getDiscardList();  // 불량/폐기
        List<DailyReportVo> useList = vo.getUseList();    // 부자재 사용량

        List<Long> deleteInIds = vo.getDeleteInIds();
        List<Long> deleteReturnIds = vo.getDeleteReturnIds();
        List<Long> deleteDiscardIds = vo.getDeleteDiscardIds();
        List<Long> deleteUseIds = vo.getDeleteUseIds();

        /*
         * 1. 마스터 저장
         * dailyId가 없을 때만 최초 1회 INSERT
         */
        if (mst.getDailyId() == null) {
            mst.setUserId(userId);
            if (dailyReportMapper.insertDailyReportMst(mst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        Long dailyId = mst.getDailyId();
        /*
         * 2. 삭제
         * 삭제할 ID가 있을 경우에만 실행
         */
        if (deleteInIds != null && !deleteInIds.isEmpty()) {
            m2DailyReportMapper.deleteDailyReport(deleteInIds);
        }
        if (deleteReturnIds != null && !deleteReturnIds.isEmpty()) {
            m2DailyReportMapper.deleteDailyReport(deleteReturnIds);
        }

        if (deleteDiscardIds != null && !deleteDiscardIds.isEmpty())
            m2DailyReportMapper.deleteDailyReport(deleteDiscardIds);
        if (deleteUseIds != null && !deleteUseIds.isEmpty()) {
            m2DailyReportMapper.deleteDailyReportUse(deleteUseIds);
        }

        /*
         * 1. 입고
         * A : 입고
         */
        saveM2List(inList, dailyId, userId, "A");

        /*
         * 2. 반품
         * P : 반품
         */
        saveM2List(returnList, dailyId, userId, "P");

        /*
         * 3. 불량/폐기
         * G : 불량/폐기
         */
        saveM2List(discardList, dailyId, userId, "G");

        /*
         * 4. 원료 사용량
         * tranTypeCd는 실제 코드에 맞게 변경
         */
        for (DailyReportVo use : useList) {
            use.setDailyId(dailyId);
            use.setUserId(userId);

            if (use.getDailySubUseId() == null) {
                if (m2DailyReportMapper.insertDailyReportUse(use) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            } else {
                if (m2DailyReportMapper.updateDailyReportUse(use) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }
        }

        return "저장되었습니다.";
    }

    private void saveM2List(List<DailyReportVo> list, Long dailyId, String userId, String tranTypeCd) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DailyReportVo item : list) {
            item.setDailyId(dailyId);
            item.setUserId(userId);
            item.setTranTypeCd(tranTypeCd);
            /*
             * PK가 없으면 신규
             */
            if (item.getDailySubItemId() == null) {
                if (m2DailyReportMapper.insertDailyReportM2(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
                /*
                 * PK가 있으면 수정
                 */
            } else {
                if (m2DailyReportMapper.updateDailyReportM2(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
    }


    /********************************** 완제품 ******************************************************************/
    public List<DailyReportVo> getM0DailyReportList(DailyReportVo vo) {
        return dailyReportMapper.getM0DailyReportList(vo);
    }

    public DailyReportRequestVo getM0DailyReportInfo(DailyReportVo vo) {
        DailyReportRequestVo mst = new DailyReportRequestVo();
        Long dailyId = vo.getDailyId();

        /*A: 입고, O :외주생산내역,  E: 외주생산비용,   P: 반품, G: 불량 : F: 출하*/
        if (dailyId == null) {
            vo.setTranTypeCd("B");
            vo.setItemTypeCd("M0");
            mst.setInList(dailyReportMapper.getInList(vo));

            vo.setTranTypeCd("P");
            mst.setReturnList(dailyReportMapper.getInList(vo));
            //출하
            vo.setTranTypeCd("F");
            mst.setShipmentList(dailyReportMapper.getInList(vo));
        } else {
            mst.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));
            vo.setDailyId(dailyId);

            vo.setTranTypeCd("A");
            mst.setInList(m0DailyReportMapper.getDailyReportList(vo));
            vo.setTranTypeCd("O");
            mst.setOutList(m0DailyReportMapper.getDailyReportList(vo));
            vo.setTranTypeCd("E");
            mst.setOutExpenseList(m0DailyReportMapper.getDailyReportList(vo));
            vo.setTranTypeCd("P");
            mst.setReturnList(m0DailyReportMapper.getDailyReportList(vo));
            vo.setTranTypeCd("G");
            mst.setDiscardList(m0DailyReportMapper.getDailyReportList(vo));
            vo.setTranTypeCd("F");
            mst.setShipmentList(m0DailyReportMapper.getDailyReportList(vo));
        }

        return mst;
    }

    @Transactional
    public String saveDailyReportM0(DailyReportRequestVo vo) {
        String userId = UserUtil.getUserId();

        /*A: 입고, O :외주생산내역,  E: 외주생산비용,   P: 반품, G: 불량 : F: 출하*/
        DailyReportVo mst = vo.getDailyReportInfo();

        List<DailyReportVo> inList = vo.getInList();       // 입고
        List<DailyReportVo> returnList = vo.getReturnList();   // 반품
        List<DailyReportVo> discardList = vo.getDiscardList();  // 불량/폐기
        List<DailyReportVo> outList = vo.getOutList();    //  외주생산내역
        List<DailyReportVo> outExpenseList = vo.getOutExpenseList();    //  외주생산비용
        List<DailyReportVo> shipmentList = vo.getShipmentList();    //  외주생산비용

        List<Long> deleteInIds = vo.getDeleteInIds();
        List<Long> deleteReturnIds = vo.getDeleteReturnIds();
        List<Long> deleteDiscardIds = vo.getDeleteDiscardIds();
        List<Long> deleteOutIds = vo.getDeleteOutIds();
        List<Long> deleteOutExpenseIds = vo.getDeleteOutExpenseIds();
        List<Long> deleteShipmentIds = vo.getDeleteShipmentIds();

        /*
         * 1. 마스터 저장
         * dailyId가 없을 때만 최초 1회 INSERT
         */
        if (mst.getDailyId() == null) {
            mst.setUserId(userId);
            if (dailyReportMapper.insertDailyReportMst(mst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        Long dailyId = mst.getDailyId();
        /*
         * 2. 삭제
         * 삭제할 ID가 있을 경우에만 실행
         */
        if (deleteInIds != null && !deleteInIds.isEmpty()) {
            m0DailyReportMapper.deleteDailyReport(deleteInIds);
        }
        if (deleteReturnIds != null && !deleteReturnIds.isEmpty()) {
            m0DailyReportMapper.deleteDailyReport(deleteReturnIds);
        }
        if (deleteDiscardIds != null && !deleteDiscardIds.isEmpty()) {
            m0DailyReportMapper.deleteDailyReport(deleteDiscardIds);
        }
        if (deleteOutIds != null && !deleteOutIds.isEmpty()) {
            m0DailyReportMapper.deleteDailyReport(deleteOutIds);
        }
        if (deleteOutExpenseIds != null && !deleteOutExpenseIds.isEmpty()) {
            m0DailyReportMapper.deleteDailyReport(deleteOutExpenseIds);
        }
        if (deleteShipmentIds != null && !deleteShipmentIds.isEmpty()) {
            m0DailyReportMapper.deleteDailyReport(deleteShipmentIds);
        }
        /*A: 입고, O :외주생산내역,  E: 외주생산비용,   P: 반품, G: 불량 : F: 출하*/
        /*
         * 1. 입고
         * A : 입고
         */
        saveM0List(inList, dailyId, userId, "A");

        /*
         * 2. 반품
         * P : 반품
         */
        saveM0List(returnList, dailyId, userId, "P");

        /*
         * 3. 불량/폐기
         * G : 불량/폐기
         */
        saveM0List(discardList, dailyId, userId, "G");
        /*
         * 3. 외주생산내역
         * O : 외주생산내역
         */
        saveM0List(outList, dailyId, userId, "O");
        /*
         * 3. 외주생산비용
         * E : 외주생산비용
         */
        saveM0List(outExpenseList, dailyId, userId, "E");

        /*
         * 3. 출하
         * F : 출하
         */
        saveM0List(shipmentList, dailyId, userId, "F");


        return "저장되었습니다.";
    }

    private void saveM0List(List<DailyReportVo> list, Long dailyId, String userId, String tranTypeCd) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DailyReportVo item : list) {
            item.setDailyId(dailyId);
            item.setUserId(userId);
            item.setTranTypeCd(tranTypeCd);
            /*
             * PK가 없으면 신규
             */
            if (item.getDailyPackingId() == null) {
                if (m0DailyReportMapper.insertDailyReportM0(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            } else {
                if (m0DailyReportMapper.updateDailyReportM0(item) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
    }


    /********************************** 인건비 ******************************************************************/
    public List<DailyLaborCostVo> getDailyLaborCostList(DailyReportVo vo) {
        return laborCostsMapper.getDailyLaborCostList(vo);
    }

    public LaborCostRequestVo getLaborCostInfo(DailyLaborCostVo vo) {
        LaborCostRequestVo req = new LaborCostRequestVo();
        Long dailyId = vo.getDailyId();

        if (dailyId == null) {
            vo.setProcCd("PRC001");
            vo.setProcStatus("12");
            req.setWeighList(dailyReportMapper.getProcList(vo));    // 칭량
            vo.setProcCd("PRC002");
            vo.setProcStatus("22");
            req.setMatList(dailyReportMapper.getProcList(vo));     // 제조
            vo.setProcCd("PRC003");
            vo.setProcStatus("32");
            req.setCoatingList(dailyReportMapper.getProcList(vo));  // 코팅
            vo.setProcCd("PRC004");
            vo.setProcStatus("42");
            req.setChargeList(dailyReportMapper.getProcList(vo));  // 충전
            vo.setProcCd("PRC005");
            vo.setProcStatus("52");
            req.setPackingList(dailyReportMapper.getProcList(vo));  // 포장

        } else {
            req.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));

            req.setWeighList(laborCostsMapper.getLaborCostProcList("PRC001", dailyId));    // 칭량
            req.setMatList(laborCostsMapper.getLaborCostProcList("PRC002", dailyId));     // 제조
            req.setCoatingList(laborCostsMapper.getLaborCostProcList("PRC003", dailyId));  // 코팅
            req.setChargeList(laborCostsMapper.getLaborCostProcList("PRC004", dailyId));  // 충전
            req.setPackingList(laborCostsMapper.getLaborCostProcList("PRC005", dailyId));  // 포장
        }

        return req;
    }

    public List<DailyLaborCostVo> getLaborCostList() {
        return laborCostsMapper.getLaborCostList();
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveLaborCostInfo(LaborCostRequestVo vo) {
        String userId = UserUtil.getUserId();

        DailyReportVo mst = vo.getDailyReportInfo();
        mst.setUserId(userId);

        if (mst.getDailyId() == null  ) {
            dailyReportMapper.insertDailyReportMst(mst);
        }else{
            dailyReportMapper.updateDailyReportMst(mst);
        }

        // 공정별 인건비 저장
        saveLaborCostList(vo.getWeighList(), mst.getDailyId(),  "PRC001", userId);  // 칭량
        saveLaborCostList(vo.getMatList(),   mst.getDailyId(),   "PRC002", userId);  // 제조
        saveLaborCostList(vo.getCoatingList(), mst.getDailyId(), "PRC003", userId);  // 코팅
        saveLaborCostList(vo.getChargeList(), mst.getDailyId(), "PRC004", userId);  // 충전
        saveLaborCostList(vo.getPackingList(), mst.getDailyId(), "PRC005", userId);  // 포장

        return "저장되었습니다.";
    }

    /**
     * 공정별 인건비 저장
     */
    private void saveLaborCostList(List<DailyLaborCostVo> list, Long dailyId, String procCd, String userId) {
        if (list == null || list.isEmpty()) {
            return;
        }

        for (DailyLaborCostVo item : list) {
            item.setDailyId(dailyId);
            item.setProcCd(procCd);
            item.setUserId(userId);

            int result;

            if (item.getDailyCostId() == null) {
                result = laborCostsMapper.insertLaborCost(item);
            } else {
                result = laborCostsMapper.updateLaborCost(item);
            }

            if (result <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
    }


    /********************************** 통합관리대장 ******************************************************************/

    public List<DailyMgmtVo> getDailyMgmtList(DailyReportVo vo){
        return  dailyReportMapper.getDailyMgmtList(vo);
    }

    public DailyMgmtVo getDailyMgmtInfo(Long dailyId){
        return  dailyReportMapper.getDailyMgmtInfo(dailyId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveDailyMgmt(DailyMgmtVo vo){
        String userId = UserUtil.getUserId();
        DailyReportVo mst = new DailyReportVo();
        vo.setUserId(userId);

        /*
         * 1. 마스터 저장
         * dailyId가 없을 때만 최초 1회 INSERT
         */
        if (vo.getDailyId() == null) {
            mst.setTypeCd("M");
            mst.setEndYn("N");
            mst.setUserId(userId);
            if (dailyReportMapper.insertDailyReportMst(mst) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
            vo.setDailyId(mst.getDailyId());

            if(dailyReportMapper.insertDailyMgmt(vo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }else{
            if(dailyReportMapper.updateDailyMgmt(vo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
    }


    /********************************** 엑셀파일 출력 ******************************************************************/
    public byte[] downloadDailyReport(DailyReportVo vo) {
        if ("I".equals(vo.getTypeCd())) {
            return m1DailyReportExcel.download(vo);
        }

        if ("S".equals(vo.getTypeCd())) {
            return m2DailyReportExcel.download(vo);
        }

        if ("P".equals(vo.getTypeCd())) {
            return m0DailyReportExcel.download(vo);
        }

        if ("C".equals(vo.getTypeCd())) {
            return laborCostExcel.download(vo);
        }

         return dailyReportMgmtExcel.download(vo);
    }



}