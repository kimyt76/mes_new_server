package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.excel.*;
import com.jct.mes_new.biz.base.mapper.*;
import com.jct.mes_new.biz.base.service.DailyReportService;
import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import com.jct.mes_new.biz.base.vo.DailyReportRequestVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.biz.base.vo.LaborCostRequestVo;
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

    public DailyReportRequestVo getM1DailyReportInfo(Long dailyId) {
        DailyReportRequestVo vo = new DailyReportRequestVo();
        DailyReportVo report = new DailyReportVo();

        if (dailyId == null) {
            //입고
            report.setTranTypeCd("A");
            report.setItemTypeCd("M1");
            vo.setInList(dailyReportMapper.getInList(report));
            //반품
            report.setTranTypeCd("P");
            vo.setReturnList(dailyReportMapper.getInList(report));
            //칭량
            vo.setProdList(dailyReportMapper.getProdList());
            //원료
            vo.setUseList(dailyReportMapper.getUseList());
        } else {
            vo.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));
            report.setDailyId(dailyId);
            //입고
            report.setTranTypeCd("A");
            report.setItemTypeCd("M1");
            vo.setInList(m1DailyReportMapper.getDailyReportList(report));
            //반품
            report.setTranTypeCd("P");
            report.setItemTypeCd("M1");
            vo.setReturnList(m1DailyReportMapper.getDailyReportList(report));
            //불량
            report.setTranTypeCd("G");
            report.setItemTypeCd("M1");
            vo.setDiscardList(m1DailyReportMapper.getDailyReportList(report));
            //칭량
            vo.setProdList(m1DailyReportMapper.getProdList(report));
            //원료
            vo.setUseList(m1DailyReportMapper.getUseList(report));
            //외부출고
            report.setTranTypeCd("Z");
            report.setItemTypeCd("M1");
            vo.setOspList(m1DailyReportMapper.getDailyReportList(report));
        }

        return vo;
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


    public DailyReportRequestVo getM2DailyReportInfo(Long dailyId) {
        DailyReportRequestVo vo = new DailyReportRequestVo();
        DailyReportVo report = new DailyReportVo();

        if (dailyId == null) {
            //입고
            report.setTranTypeCd("A");
            report.setItemTypeCd("M2");
            vo.setInList(dailyReportMapper.getInList(report));
            //반품
            report.setTranTypeCd("P");
            vo.setReturnList(dailyReportMapper.getInList(report));
            //사용량
            vo.setUseList(dailyReportMapper.getUseM2List());
        } else {
            vo.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));
            report.setDailyId(dailyId);
            //입고
            report.setTranTypeCd("A");
            report.setItemTypeCd("M2");
            vo.setInList(m2DailyReportMapper.getDailyReportList(report));
            //반품
            report.setTranTypeCd("P");
            report.setItemTypeCd("M2");
            vo.setReturnList(m2DailyReportMapper.getDailyReportList(report));
            //불량
            report.setTranTypeCd("G");
            report.setItemTypeCd("M2");
            vo.setDiscardList(m2DailyReportMapper.getDailyReportList(report));
            //사용량
            vo.setUseList(m2DailyReportMapper.getUseList(report));
        }

        return vo;
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

    public DailyReportRequestVo getM0DailyReportInfo(Long dailyId) {
        DailyReportRequestVo vo = new DailyReportRequestVo();
        DailyReportVo report = new DailyReportVo();

        /*A: 입고, O :외주생산내역,  E: 외주생산비용,   P: 반품, G: 불량 : F: 출하*/
        if (dailyId == null) {
            report.setTranTypeCd("A");
            report.setItemTypeCd("M0");
            vo.setInList(dailyReportMapper.getInList(report));

            report.setTranTypeCd("P");
            vo.setInList(dailyReportMapper.getInList(report));
            //출하
            report.setTranTypeCd("F");
            vo.setReturnList(dailyReportMapper.getInList(report));
        } else {
            vo.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));
            report.setDailyId(dailyId);

            report.setTranTypeCd("A");
            vo.setInList(m0DailyReportMapper.getDailyReportList(report));
            report.setTranTypeCd("O");
            vo.setOutList(m0DailyReportMapper.getDailyReportList(report));
            report.setTranTypeCd("E");
            vo.setOutExpenseList(m0DailyReportMapper.getDailyReportList(report));
            report.setTranTypeCd("P");
            vo.setReturnList(m0DailyReportMapper.getDailyReportList(report));
            report.setTranTypeCd("G");
            vo.setDiscardList(m0DailyReportMapper.getDailyReportList(report));
            report.setTranTypeCd("F");
            vo.setShipmentList(m0DailyReportMapper.getDailyReportList(report));
        }

        return vo;
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
    public List<DailyLaborCostVo> getDailyLaborCostList(DailyLaborCostVo vo) {
        return laborCostsMapper.getDailyLaborCostList(vo);
    }

    public LaborCostRequestVo getLaborCostInfo(Long dailyId) {
        LaborCostRequestVo vo = new LaborCostRequestVo();
        String procCd = "";
        String procStatus = "";
        ;

        if (dailyId == null) {
            vo.setWeighList(dailyReportMapper.getProcList("PRC001", "12"));    // 칭량
            vo.setMatList(dailyReportMapper.getProcList("PRC002", "22"));     // 제조
            vo.setCoatingList(dailyReportMapper.getProcList("PRC003", "32"));  // 코팅
            vo.setChargeList(dailyReportMapper.getProcList("PRC004", "42"));  // 충전
            vo.setPackingList(dailyReportMapper.getProcList("PRC005", "52"));  // 포장

        } else {
            vo.setDailyReportInfo(dailyReportMapper.getDailyReportMst(dailyId));

            vo.setWeighList(laborCostsMapper.getProcList("PRC001", "12"));    // 칭량
            vo.setMatList(laborCostsMapper.getProcList("PRC002", "22"));     // 제조
            vo.setCoatingList(laborCostsMapper.getProcList("PRC003", "32"));  // 코팅
            vo.setChargeList(laborCostsMapper.getProcList("PRC004", "42"));  // 충전
            vo.setPackingList(laborCostsMapper.getProcList("PRC005", "52"));  // 포장
        }

        return vo;
    }


    /********************************** 통합관리대장 ******************************************************************/

    /********************************** 엑셀파일 출력 ******************************************************************/
    public byte[] downloadDailyReport(DailyReportVo vo) {
        if ("M".equals(vo.getTypeCd())) {
            return m1DailyReportExcel.download(vo);
        }

        if ("S".equals(vo.getTypeCd())) {
            return m2DailyReportExcel.download(vo);
        }

        if ("P".equals(vo.getTypeCd())) {
            return m0DailyReportExcel.download(vo);
        }

//        if ("C".equals(vo.getTypeCd())) {
//            return laborCostExcel.download(vo);
//        }
//
 //        return totalDailyReportExcel.download(vo);
        return m0DailyReportExcel.download(vo);
    }



}