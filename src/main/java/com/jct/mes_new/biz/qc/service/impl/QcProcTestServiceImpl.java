package com.jct.mes_new.biz.qc.service.impl;

import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.qc.mapper.QcProcTestMapper;
import com.jct.mes_new.biz.qc.service.QcProcTestService;
import com.jct.mes_new.biz.qc.vo.*;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.CommonUtil;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.ComUtil;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class QcProcTestServiceImpl implements QcProcTestService {

    private final QcProcTestMapper qcProcTestMapper;
    private final WorkOrderMapper workOrderMapper ;

    private static final DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd");



    public List<QcProcTestVo> getQcProcTestList(QcProcTestVo vo){
        return qcProcTestMapper.getQcProcTestList(vo);
    }

    public String createQcProcTestInfo(QcProcTestVo vo){
        vo.setUserId(UserUtil.getUserId());
        //마스터
        if(qcProcTestMapper.createQcProcTestInfo(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        return "저장했습니다.";
    }


    public QcProcTestRequestVo getQcProcTestTabInfo(QcProcTestVo vo){
        QcProcTestRequestVo resultVo = new QcProcTestRequestVo();
        String procCd = this.getProcCd(vo.getBatchStatus());
        if("S".equals(vo.getTestType()) ) {
            resultVo.setQcProcTestMst(qcProcTestMapper.getQcProcTestMst(vo.getWorkBatchId(), procCd) );
            resultVo.setQcProcSample(qcProcTestMapper.getQcProcSample(vo.getTestType(), vo.getQcProcTestMstId()));
        }else if ( "QRC003".equals(vo.getTestType()) || "QRC004".equals(vo.getTestType()) || "QRC005".equals(vo.getTestType()) ) {
            resultVo.setMethodList(qcProcTestMapper.getMethodList(vo.getTestType(), vo.getQcProcTestMstId()));
        }else {
            resultVo.setDetailList(qcProcTestMapper.getDetailList(vo.getTestType(), vo.getQcProcTestMstId()));
            resultVo.setLineList(qcProcTestMapper.getLineList(vo.getTestType(), vo.getQcProcTestMstId()));
        }

        return resultVo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveQcProcTestTabInfo(QcProcTestRequestVo vo) {
        String userId = UserUtil.getUserId();
        QcProcTestVo mst = vo.getQcProcTestMst();

        if (mst == null || mst.getTestType() == null ) {
            throw new IllegalArgumentException("testType이 없습니다.");
        }

        String testType = mst.getTestType();
        Long qcProcTestMstId = mst.getQcProcTestMstId();
        /* =============================================
         * Master
         * ============================================= */
        mst.setUserId(userId);
        if (qcProcTestMapper.updateQcProcTest(mst) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        /* =============================================
         * 탭별 저장
         * ============================================= */
        switch (testType) {
            /* =========================================
             * 검체채취
             * ========================================= */
            case "S" ->
                    saveSample(vo.getQcProcSample(), qcProcTestMstId, userId);
            /* =========================================
             * QRC
             * ========================================= */
            case "QRC003",
                 "QRC004",
                 "QRC005" ->
                    saveMethodList(vo.getMethodList(), qcProcTestMstId, testType, userId);
            /* =========================================
             * Detail
             * ========================================= */

            case "WE115",
                 "WE613",
                 "WE616",
                 "GA115",
                 "CA515",
                 "ES515" ->
                    saveDetailList(vo.getDetailList(), qcProcTestMstId, testType, userId);
            default ->
                    throw new IllegalArgumentException(
                            "지원하지 않는 testType입니다. "+ testType
                    );
        }

        return "저장되었습니다.";
    }

    public String saveQcProcTestLineList(QcProcTestRequestVo vo){
        String userId = UserUtil.getUserId();
        QcProcTestVo mst = vo.getQcProcTestMst();
        List<QcProcLinelVo> lineList = vo.getLineList();
        for(QcProcLinelVo lineVo : lineList ){
            lineVo.setUserId(userId);
            lineVo.setQcProcTestMstId(mst.getQcProcTestMstId());
            if ( lineVo.getQcProcTestLineId() == null ) {
                if(qcProcTestMapper.insertQcProcTestLine(lineVo) <= 0 ) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                if(qcProcTestMapper.updateQcProcTestLine(lineVo) <= 0 ) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return "저장되었습니다.";
    }

    private void saveSample(
            QcProcSampleVo sample,
            Long qcProcTestMstId,
            String userId ) {

        if (sample == null) {
            return;
        }
        sample.setUserId(userId);
        // 신규일 경우 FK 설정
        if (sample.getQcProcTestSampleId() == null) {
            sample.setQcProcTestMstId(qcProcTestMstId);
            if (qcProcTestMapper.insertQcProcTestSample(sample) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        } else {
            if (qcProcTestMapper.updateQcProcTestSample(sample) <= 0) {
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        }
    }

    private void saveMethodList(
            List<QcProcMethodVo> methodList,
            Long qcProcTestMstId,
            String testType,
            String userId
    ) {

        if (methodList == null || methodList.isEmpty()) {
            return;
        }

        for (QcProcMethodVo method : methodList) {
            method.setUserId(userId);
            method.setTestType(testType);
            if (method.getQcProcTestMethodId() == null) {
                method.setQcProcTestMstId(qcProcTestMstId);
                if (qcProcTestMapper.insertQcProcTestMethod(method) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            } else {
                if (qcProcTestMapper.updateQcProcTestMethod(method) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
    }

    private void saveDetailList(
            List<QcProcDetailVo> detailList,
            Long qcProcTestMstId,
            String testType,
            String userId   ) {

        if (detailList == null || detailList.isEmpty()) {
            return;
        }
        for (QcProcDetailVo detail : detailList) {
            detail.setUserId(userId);
            detail.setTestType(testType);
            if (detail.getQcProcTestDetailId() == null) {
                detail.setQcProcTestMstId(qcProcTestMstId);
                if (qcProcTestMapper.insertQcProcTestDetail(detail) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            } else {
                if (qcProcTestMapper.updateQcProcTestDetail(detail) <= 0) {
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
    }

    public String getProcCd(String status){
        String procCd = "";

        if ( "31".equals(status) || "32".equals(status) ) {
            procCd = "PRC003";
        }else if ( "41".equals(status) || "42".equals(status) ) {
            procCd = "PRC004";
        }else if ( "51".equals(status) || "52".equals(status) ) {
            procCd = "PRC005";
        }

        return procCd;
    }

    public byte[] downloadQcProcTest(Long qcProcTestMstId) throws Exception {
        QcProcTestVo vo =  qcProcTestMapper.getQcProcTestInfo(qcProcTestMstId);
        String procCd  =  this.getProcCd(vo.getBatchStatus());

        QcProcTestVo mst = qcProcTestMapper.getQcProcTestMst(vo.getWorkBatchId(), procCd);
        if (mst == null) {
            throw new Exception("공정검사 Master 정보가 없습니다.");
        }

        String qcType = mst.getQcTestType();

        if (qcType == null) {throw new Exception("공정검사 타입이 없습니다.");}

        switch (qcType) {
            case "A":
                return getExcelTypeA(mst);
            case "B":
                return getExcelTypeB(mst);
            case "C":
                return getExcelTypeC(mst);
            case "D":
                return getExcelTypeD(mst);
            default:
                throw new Exception("지원하지 않는 QC 타입입니다. : " + qcType);
        }
    }

    private byte[] getExcelTypeA(QcProcTestVo mst) throws Exception {
        Long qcProcTestMstId = mst.getQcProcTestMstId();
        /*
         * ==========================================
         * 데이터 조회
         * ==========================================
         */
        // 충전 공정
        List<QcProcMethodVo> methodList = qcProcTestMapper.getMethodList("QRC004", qcProcTestMstId);
        // 포장 공정
        List<QcProcMethodVo> packingMethods = qcProcTestMapper.getMethodList("QRC005", qcProcTestMstId);
        // 충전 작업지시
        //WorkOrderItemView chargingItem = workOrderItemViewService.getById(mst.getChargingId());
        WorkOrderInfoVo chargingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC004");
        // 포장 작업지시
        //WorkOrderItemView packingItem = workOrderItemViewService.getById(mst.getPackingId());
        WorkOrderInfoVo packingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC005");
        // WE616 라인
        List<QcProcLinelVo> lineList = qcProcTestMapper.getLineList("WE616", qcProcTestMstId);
        // WE616 검사 데이터
        List<QcProcDetailVo> detailList = qcProcTestMapper.getDetailList("WE616", qcProcTestMstId);
        // 검체채취
        QcProcSampleVo sampleData = qcProcTestMapper.getQcProcSample("S", qcProcTestMstId);

        /*
         * ==========================================
         * Excel 생성
         * ==========================================
         */
        try (
                InputStream excelStream = getClass().getResourceAsStream("/excel/qc_proc_test_type_a.xlsx")
        ) {
            if (excelStream == null) {
                throw new Exception("Excel Template을 찾을 수 없습니다. "+ "/excel/qc_proc_test_type_a.xlsx");
            }
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);

            try {
                /*
                 * ======================================
                 * 상단 기본정보
                 * ======================================
                 */
                String[] infoData = {
                        ComUtil.nvl(mst.getItemCd())
                        ,"JQP12-01"
                        ,"(Rev.02)",
                        ComUtil.nvl(mst.getItemName()),
                        ComUtil.nvl(mst.getClientName()),
                        mst.getProdDate() != null? sdf.format(mst.getProdDate()): "",
                        ComUtil.nvl(mst.getMakeNo()),
                        ComUtil.nvl(mst.getDisplayCapacity()),
                        getLotNo(mst),
                        ComUtil.nvl(mst.getTesterId())
                };
                /*
                 * ======================================
                 * Sheet1
                 * ======================================
                 */
                Sheet sheet1 = workbook.getSheet("Sheet1");
                String[] sheet1Info = {"AH1", "AH3", "AH4", "F6", "AH6", "F7", "T7", "AH7", "F8", "AH8"};

                for (int i = 0; i < sheet1Info.length; i++ ) {
                    ExcelStyleUtil.getCellRef(sheet1,sheet1Info[i]).setCellValue(infoData[i]);
                }
                /*
                 * ======================================
                 * 충전작업
                 * ======================================
                 */
                String chargingStartTime = chargingItem != null
                                && chargingItem.getWorkStartTime() != null
                                ? sdf.format(chargingItem.getWorkStartTime()): "";

                String chargingEndTime =
                        chargingItem != null&& chargingItem.getWorkEndTime() != null
                                ? sdf.format(chargingItem.getWorkEndTime()): "";

                ExcelStyleUtil.getCellRef(sheet1,"Q11").setCellValue(chargingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X11").setCellValue(chargingEndTime);

                int chargingRow = 13;

                for (QcProcMethodVo item : methodList ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + chargingRow).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + chargingRow).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"AD" + chargingRow).setCellValue(getMethodResult(item.getTestResult()));
                    chargingRow++;
                }
                /*
                 * ======================================
                 * WE616 중량검사
                 * ======================================
                 */

                ExcelStyleUtil.getCellRef(sheet1,"AH21").setCellValue(ComUtil.nvl(chargingItem.getOrderQty()));
                String[] lineNameCells = {"I23", "M23", "Q23", "U23", "Y23", "AC23"};
                setLineNames(sheet1,lineNameCells,lineList);
                /*
                 * Detail
                 */
                int detailRow = 24;

                for ( QcProcDetailVo item : detailList ) {
                    ExcelStyleUtil.getCellRef(sheet1,"E" + detailRow).setCellValue(emptyTime(item.getTestTime()));
                    setWeightCell(sheet1,"I" + detailRow,item.getLine1());
                    setWeightCell(sheet1,"M" + detailRow,item.getLine2());
                    setWeightCell(sheet1,"Q" + detailRow,item.getLine3());
                    setWeightCell(sheet1,"U" + detailRow,item.getLine4());
                    setWeightCell(sheet1,"Y" + detailRow,item.getLine5());
                    setWeightCell(sheet1,"AC" + detailRow,item.getLine6());
                    ExcelStyleUtil.getCellRef(sheet1,"AG" + detailRow).setCellValue(getPassResult(item.getPassYn(),true));
                    detailRow++;
                }
                /*
                 * ======================================
                 * 포장작업
                 * ======================================
                 */
                String packingStartTime = packingItem != null&& packingItem.getWorkStartTime() != null
                                ? sdf.format(packingItem.getWorkStartTime()): "";
                String packingEndTime =packingItem != null
                                && packingItem.getWorkEndTime() != null
                                ? sdf.format(packingItem.getWorkEndTime()): "";
                ExcelStyleUtil.getCellRef(sheet1,"Q42").setCellValue(packingStartTime);
                ExcelStyleUtil
                        .getCellRef(
                                sheet1,
                                "X42"
                        )
                        .setCellValue(
                                packingEndTime
                        );

                int packingRow = 44;

                for (QcProcMethodVo item : packingMethods ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + packingRow).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + packingRow).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"AD" + packingRow).setCellValue(getMethodResult(item.getTestResult()));
                    packingRow++;
                }
                /*
                 * 비고
                 */
                ExcelStyleUtil.getCellRef(sheet1,"A52").setCellValue(ComUtil.nvl(mst.getEtc()));

                /*
                 * ======================================
                 * Sheet2 검체채취
                 * ======================================
                 */
                Sheet sheet2 = workbook.getSheet("Sheet2");

                String[] sheet2Info = {"AD1", "AD3", "AD4", "F6", "AD6", "F7", "R7", "AD7", "F8","AD8"};

                for (int i = 0; i < sheet2Info.length; i++ ) {
                    ExcelStyleUtil.getCellRef(sheet2,sheet2Info[i]).setCellValue(infoData[i]);
                }
                setSampleData(sheet2,sampleData);
                /*
                 * ======================================
                 * byte[] 반환
                 * ======================================
                 */
                return ExcelStyleUtil.toByteArray(workbook);

            }finally {
                workbook.close();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("엑셀파일 생성중 에러발생!", ex);
        }
    }

    private byte[] getExcelTypeB(QcProcTestVo mst) throws Exception {
        Long qcProcTestMstId = mst.getQcProcTestMstId();
        /*
         * ==========================================
         * 데이터 조회
         * ==========================================
         */
        //코팅 공정
        List<QcProcMethodVo> coatingList = qcProcTestMapper.getMethodList("QRC003", qcProcTestMstId);
        // 충전 공정
        List<QcProcMethodVo> methodList = qcProcTestMapper.getMethodList("QRC004", qcProcTestMstId);
        // 포장 공정
        List<QcProcMethodVo> packingMethods = qcProcTestMapper.getMethodList("QRC005", qcProcTestMstId);
        // 코팅 작업지시
        WorkOrderInfoVo coatingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC003");
        // 충전 작업지시
        WorkOrderInfoVo chargingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC004");
        // 포장 작업지시
        WorkOrderInfoVo packingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC005");
        // WE115 라인
        List<QcProcLinelVo> lineList = qcProcTestMapper.getLineList("WE115", qcProcTestMstId);
        // WE616 검사 데이터
        List<QcProcDetailVo> WE115_details = qcProcTestMapper.getDetailList("WE616", qcProcTestMstId);
        // GA115 검사 데이터
        List<QcProcDetailVo> GA115_details = qcProcTestMapper.getDetailList("GA115", qcProcTestMstId);
        // 검체채취
        QcProcSampleVo sampleData = qcProcTestMapper.getQcProcSample("S", qcProcTestMstId);

        /*
         * ==========================================
         * Excel 생성
         * ==========================================
         */
        try (
                InputStream excelStream = getClass().getResourceAsStream("/excel/qc_proc_test_type_b.xlsx")
        ) {
            if (excelStream == null) {
                throw new Exception("Excel Template을 찾을 수 없습니다. "+ "/excel/qc_proc_test_type_b.xlsx");
            }
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);

            try {
                /*
                 * ======================================
                 * 상단 기본정보
                 * ======================================
                 */
                String[] infoData = {
                        ComUtil.nvl(mst.getItemCd())
                        ,"JQP12-01"
                        ,"(Rev.03)",
                        ComUtil.nvl(mst.getItemName()),
                        ComUtil.nvl(mst.getClientName()),
                        mst.getProdDate() != null? sdf.format(mst.getProdDate()): "",
                        ComUtil.nvl(mst.getMakeNo()),
                        ComUtil.nvl(mst.getDisplayCapacity()),
                        getLotNo(mst),
                        ComUtil.nvl(mst.getTesterId())
                };
                /*
                 * ======================================
                 * Sheet1
                 * ======================================
                 */
                Sheet sheet1 = workbook.getSheet("Sheet1");
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                String[] sheet1Info = {"AH1", "AH3", "AH4", "F6", "AH6", "F7", "T7", "AH7", "F8", "AH8"};

                for (int i = 0; i < sheet1Info.length; i++ ) {
                    ExcelStyleUtil.getCellRef(sheet1,sheet1Info[i]).setCellValue(infoData[i]);
                }
                /*
                 * ======================================
                 * 코팅작업
                 * ======================================
                 */
                //작업일자 (시작일시)Q11
                String coating_startTime = (coatingItem.getWorkStartTime() != null)? sdf.format(coatingItem.getWorkStartTime()) : "";
                ExcelStyleUtil.getCellRef(sheet1, "Q10").setCellValue(coating_startTime);
                //작업일자 (종료일시)X11
                String coating_endTime = (coatingItem.getWorkEndTime() != null)? sdf.format(coatingItem.getWorkEndTime()) : "";
                ExcelStyleUtil.getCellRef(sheet1, "X10").setCellValue(coating_endTime);

                int idx1 = 12;

                for (QcProcMethodVo item : coatingList){
                    ExcelStyleUtil.getCellRef(sheet1, "C" + idx1).setCellValue(item.getTestMethod());
                    ExcelStyleUtil.getCellRef(sheet1, "O" + idx1).setCellValue(item.getTestItem());
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + idx1).setCellValue(getMethodResult(item.getTestResult()));
                    idx1++;
                }

                /*
                 * ======================================
                 * 충전작업
                 * ======================================
                 */
                String chargingStartTime = chargingItem != null
                        && chargingItem.getWorkStartTime() != null
                        ? sdf.format(chargingItem.getWorkStartTime()): "";

                String chargingEndTime =
                        chargingItem != null&& chargingItem.getWorkEndTime() != null
                                ? sdf.format(chargingItem.getWorkEndTime()): "";

                ExcelStyleUtil.getCellRef(sheet1,"Q17").setCellValue(chargingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X17").setCellValue(chargingEndTime);

                int chargingRow = 20;

                for (QcProcMethodVo item : methodList ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + chargingRow).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + chargingRow).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + chargingRow).setCellValue(getMethodResult(item.getTestResult()));
                    chargingRow++;
                }

                //충전지시량 AD27
                ExcelStyleUtil.getCellRef(sheet1, "AD28").setCellValue(ComUtil.nvl(chargingItem.getOrderQty()));

                //todo 중량검사 15개
                // 검사시간, 수량, 검사결과 "□ 적합 □ 부적합", "■ 적합 □ 부적합", "□ 적합 ■ 부적합"
                String[] cell1 = { "B31", "B32", "B33", "B34", "B35", "M31", "M32", "M33", "M34", "M35", "Y31", "Y32", "Y33", "Y34", "Y35" };
                String[] cell2 = { "E31", "E32", "E33", "E34", "E35", "Q31", "Q32", "Q33", "Q34", "Q35", "AB31", "AB32", "AB33", "AB34", "AB35",};
                String[] cell3 = { "I31", "I32", "I33", "I34", "I35", "U31", "U32", "U33", "U34", "U35", "AF31", "AF32", "AF33", "AF34", "AF35",};

                for (int i = 0; i < cell1.length; i++) {
                    QcProcDetailVo items = WE115_details.get(i);
                    //검사시간
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet1, cell1[i]).setCellValue(testTime);
                    //수량
                    String line1 = (items.getLine1() != null) ? items.getLine1().toString() + " g" : "g";
                    ExcelStyleUtil.getCellRef(sheet1, cell2[i]).setCellValue(line1);
                    //검사결과
                    ExcelStyleUtil.getCellRef(sheet1, cell3[i]).setCellValue(getPassResult(items.getPassYn(),true) );
                }

                /** 겔수량검사 **/
                //충전 매 수 AD38
                ExcelStyleUtil.getCellRef(sheet1, "AD39").setCellValue(chargingItem.getChargingCnt());
                //todo 겔수량검사 15개
                // 검사시간, 수량, 검사결과 "□ 적합 □ 부적합", "■ 적합 □ 부적합", "□ 적합 ■ 부적합"
                String[] cell4 = { "B42", "B43", "B44", "B45", "B46", "M42", "M43", "M44", "M45", "M46", "Y42", "Y43", "Y44", "Y45", "Y46" };
                String[] cell5 = { "E42", "E43", "E44", "E45", "E46", "Q42", "Q43", "Q44", "Q45", "Q46", "AB42", "AB43", "AB44", "AB45", "AB46",};
                String[] cell6 = { "I42", "I43", "I44", "I45", "I46", "U42", "U43", "U44", "U45", "U46", "AF42", "AF43", "AF44", "AF45", "AF46",};

                for (int i = 0; i < cell1.length; i++) {
                    QcProcDetailVo items = GA115_details.get(i);
                    //검사시간
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet1, cell4[i]).setCellValue(testTime);
                    //수량
                    String line1 = (items.getLine1() != null) ? items.getLine1().setScale(0).toString() + " 매" : "매";
                    ExcelStyleUtil.getCellRef(sheet1, cell5[i]).setCellValue(line1);
                    //검사결과
                    ExcelStyleUtil.getCellRef(sheet1, cell6[i]).setCellValue(getPassResult(items.getPassYn(),true) );
                }
                /*
                 * ======================================
                 * 포장작업
                 * ======================================
                 */
                String packingStartTime = packingItem != null&& packingItem.getWorkStartTime() != null
                        ? sdf.format(packingItem.getWorkStartTime()): "";
                String packingEndTime =packingItem != null
                        && packingItem.getWorkEndTime() != null
                        ? sdf.format(packingItem.getWorkEndTime()): "";
                ExcelStyleUtil.getCellRef(sheet1,"Q49").setCellValue(packingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X49").setCellValue(packingEndTime);

                int packingRow = 51;

                for (QcProcMethodVo item : packingMethods ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + packingRow).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + packingRow).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + packingRow).setCellValue(getMethodResult(item.getTestResult()));
                    packingRow++;
                }
                /*
                 * 비고
                 */
                ExcelStyleUtil.getCellRef(sheet1,"A59").setCellValue(ComUtil.nvl(mst.getEtc()));

                /** Sheet2 : 검체채취기준 **/
                /*
                 * ======================================
                 * Sheet2 검체채취
                 * ======================================
                 */
                Sheet sheet2 = workbook.getSheet("Sheet2");
                /*상단 정보영역*/
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                String[] sheet2Info = { "AD1", "AD3", "AD4", "F6", "AD6", "F7", "R7", "AD7", "F8", "AD8" };

                Boolean isSample = sampleData != null;

                for (int i = 0; i < sheet1Info.length; i++) {
                    ExcelStyleUtil.getCellRef(sheet2, sheet2Info[i]).setCellValue(infoData[i]);
                }
                //채취일자 Q29
                ExcelStyleUtil.getCellRef(sheet2, "Q29").setCellValue((isSample && sampleData.getSampleDate() != null) ? sampleData.getSampleDate() : "");
                setSampleData(sheet2,sampleData);
                /*
                 * ======================================
                 * byte[] 반환
                 * ======================================
                 */
                return ExcelStyleUtil.toByteArray(workbook);

            }finally {
                workbook.close();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("엑셀파일 생성중 에러발생!", ex);
        }
    }

    private byte[] getExcelTypeC(QcProcTestVo mst) throws Exception {
        Long qcProcTestMstId = mst.getQcProcTestMstId();

        /*
         * ==========================================
         * 데이터 조회
         * ==========================================
         */
        //코팅 공정
        List<QcProcMethodVo> coatingList = qcProcTestMapper.getMethodList("QRC003", qcProcTestMstId);
        // 충전 공정
        List<QcProcMethodVo> methodList = qcProcTestMapper.getMethodList("QRC004", qcProcTestMstId);
        // 포장 공정
        List<QcProcMethodVo> packingMethods = qcProcTestMapper.getMethodList("QRC005", qcProcTestMstId);
        // 코팅 작업지시
        WorkOrderInfoVo coatingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC003");
        // 충전 작업지시
        WorkOrderInfoVo chargingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC004");
        // 포장 작업지시
        WorkOrderInfoVo packingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC005");
        // WE115 검사 데이터
        List<QcProcDetailVo> WE115_details = qcProcTestMapper.getDetailList("WE115", qcProcTestMstId);
        //GA115 data
        List<QcProcDetailVo> GA115_details = qcProcTestMapper.getDetailList("GA115", qcProcTestMstId);
        //CA515 data
        List<QcProcDetailVo> CA515_details = qcProcTestMapper.getDetailList("CA515" , qcProcTestMstId);
        //ES515 data
        List<QcProcDetailVo> ES515_details = qcProcTestMapper.getDetailList("ES515", qcProcTestMstId);
        //CA515 line setting
        List<QcProcLinelVo> CA515_line = qcProcTestMapper.getLineList("CA515", qcProcTestMstId);
        //ES515 line setting
        List<QcProcLinelVo> ES515_line = qcProcTestMapper.getLineList("ES515", qcProcTestMstId);
        // 검체채취
        QcProcSampleVo sampleData = qcProcTestMapper.getQcProcSample("S", qcProcTestMstId);
        /*
         * ==========================================
         * Excel 생성
         * ==========================================
         */
        try (
                InputStream excelStream = getClass().getResourceAsStream("/excel/qc_proc_test_type_c.xlsx")
        ) {
            if (excelStream == null) {
                throw new Exception("Excel Template을 찾을 수 없습니다. "+ "/excel/qc_proc_test_type_c.xlsx");
            }
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);

            try {
                /*
                 * ======================================
                 * 상단 기본정보
                 * ======================================
                 */
                String[] infoData = {
                        ComUtil.nvl(mst.getItemCd())
                        ,"JQP12-01"
                        ,"(Rev.02)",
                        ComUtil.nvl(mst.getItemName()),
                        ComUtil.nvl(mst.getClientName()),
                        mst.getProdDate() != null? sdf.format(mst.getProdDate()): "",
                        ComUtil.nvl(mst.getMakeNo()),
                        ComUtil.nvl(mst.getDisplayCapacity()),
                        getLotNo(mst),
                        ComUtil.nvl(mst.getTesterId()),
                        (mst.getWorkFlow() != null) ? mst.getWorkFlow() : "" //포장공정도
                };
                /*
                 * ======================================
                 * Sheet1
                 * ======================================
                 */
                Sheet sheet1 = workbook.getSheet("Sheet1");
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                //비고 A58
                ExcelStyleUtil.getCellRef(sheet1, "A58").setCellValue(mst.getEtc());

                /*상단 정보영역*/
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자, 포장공정도
                String[] sheet1Info = {
                          "AD1" // 품목코드
                        , "AD3" // 문서번호(JQP12-01)
                        , "AD4" // 문서버전(Rev.01)
                        , "F6" // 제품명
                        , "AD6" // 고객사
                        , "F7" // 제조일자
                        , "R7" // 제조번호
                        , "AD7" // 표시량
                        , "F8" // 로트인쇄
                        , "AD8" // 검사자
                        , "A58" // 포장공정도
                };
                for (int i = 0; i < sheet1Info.length; i++) {
                    ExcelStyleUtil.getCellRef(sheet1, sheet1Info[i]).setCellValue(infoData[i]);
                }

                /*
                 * ======================================
                 * 코팅작업
                 * ======================================
                 */
                //작업일자 (시작일시)Q11
                String coating_startTime = (coatingItem.getWorkStartTime() != null)? sdf.format(coatingItem.getWorkStartTime()) : "";
                ExcelStyleUtil.getCellRef(sheet1, "Q10").setCellValue(coating_startTime);
                //작업일자 (종료일시)X11
                String coating_endTime = (coatingItem.getWorkEndTime() != null)? sdf.format(coatingItem.getWorkEndTime()) : "";
                ExcelStyleUtil.getCellRef(sheet1, "X10").setCellValue(coating_endTime);

                int idx1 = 12;

                for (QcProcMethodVo item : coatingList){
                    ExcelStyleUtil.getCellRef(sheet1, "C" + idx1).setCellValue(item.getTestMethod());
                    ExcelStyleUtil.getCellRef(sheet1, "O" + idx1).setCellValue(item.getTestItem());
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + idx1).setCellValue(getMethodResult(item.getTestResult()));
                    idx1++;
                }
                /*
                 * ======================================
                 * 충전작업
                 * ======================================
                 */
                String chargingStartTime = chargingItem != null
                        && chargingItem.getWorkStartTime() != null
                        ? sdf.format(chargingItem.getWorkStartTime()): "";

                String chargingEndTime =
                        chargingItem != null&& chargingItem.getWorkEndTime() != null
                                ? sdf.format(chargingItem.getWorkEndTime()): "";

                ExcelStyleUtil.getCellRef(sheet1,"Q17").setCellValue(chargingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X17").setCellValue(chargingEndTime);

                int chargingRow = 19;

                for (QcProcMethodVo item : methodList ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + chargingRow).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + chargingRow).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + chargingRow).setCellValue(getMethodResult(item.getTestResult()));
                    chargingRow++;
                }

                //충전지시량 AD27
                ExcelStyleUtil.getCellRef(sheet1, "AD27").setCellValue(ComUtil.nvl(chargingItem.getOrderQty()));

                //todo 중량검사 15개
                // 검사시간, 수량, 검사결과 "□ 적합 □ 부적합", "■ 적합 □ 부적합", "□ 적합 ■ 부적합"
                String[] cell1 = { "B30", "B31", "B32", "B33", "B34", "M30", "M31", "M32", "M33", "M34", "Y30", "Y31", "Y32", "Y33", "Y34" };
                String[] cell2 = { "E30", "E31", "E32", "E33", "E34", "Q30", "Q31", "Q32", "Q33", "Q34", "AB30", "AB31", "AB32", "AB33", "AB34",};
                String[] cell3 = { "I30", "I31", "I32", "I33", "I34", "U30", "U31", "U32", "U33", "U34", "AF30", "AF31", "AF32", "AF33", "AF34",};

                for (int i = 0; i < cell1.length; i++) {
                    QcProcDetailVo items = WE115_details.get(i);
                    //검사시간
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet1, cell1[i]).setCellValue(testTime);
                    //수량
                    String line1 = (items.getLine1() != null) ? items.getLine1().toString() + " g" : "g";
                    ExcelStyleUtil.getCellRef(sheet1, cell2[i]).setCellValue(line1);
                    //검사결과
                    ExcelStyleUtil.getCellRef(sheet1, cell3[i]).setCellValue(getPassResult(items.getPassYn(),true) );
                }
                /** 겔수량검사 **/
                //충전 매 수 AD38
                ExcelStyleUtil.getCellRef(sheet1, "AD38").setCellValue(chargingItem.getChargingCnt());
                //todo 겔수량검사 15개
                // 검사시간, 수량, 검사결과 "□ 적합 □ 부적합", "■ 적합 □ 부적합", "□ 적합 ■ 부적합"
                String[] cell4 = { "B41", "B42", "B43", "B44", "B45", "M41", "M42", "M43", "M44", "M45", "Y41", "Y42", "Y43", "Y44", "Y45" };
                String[] cell5 = { "E41", "E42", "E43", "E44", "E45", "Q41", "Q42", "Q43", "Q44", "Q45", "AB41", "AB42", "AB43", "AB44", "AB45",};
                String[] cell6 = { "I41", "I42", "I43", "I44", "I45", "U41", "U42", "U43", "U44", "U45", "AF41", "AF42", "AF43", "AF44", "AF45",};

                for (int i = 0; i < cell1.length; i++) {
                    QcProcDetailVo items = GA115_details.get(i);
                    //검사시간
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet1, cell4[i]).setCellValue(testTime);
                    //수량
                    String line1 = (items.getLine1() != null) ? items.getLine1().setScale(0).toString() + " 매" : "매";
                    ExcelStyleUtil.getCellRef(sheet1, cell5[i]).setCellValue(line1);
                    //검사결과
                    ExcelStyleUtil.getCellRef(sheet1, cell6[i]).setCellValue(getPassResult(items.getPassYn(),true) );
                }

                /*
                 * ======================================
                 * 포장작업
                 * ======================================
                 */
                String packingStartTime = packingItem != null&& packingItem.getWorkStartTime() != null
                        ? sdf.format(packingItem.getWorkStartTime()): "";
                String packingEndTime =packingItem != null
                        && packingItem.getWorkEndTime() != null
                        ? sdf.format(packingItem.getWorkEndTime()): "";
                ExcelStyleUtil.getCellRef(sheet1,"Q48").setCellValue(packingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X48").setCellValue(packingEndTime);

                int packingRow = 51;

                for (QcProcMethodVo item : packingMethods ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + packingRow).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + packingRow).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + packingRow).setCellValue(getMethodResult(item.getTestResult()));
                    packingRow++;
                }

                /*
                 * ======================================
                 * Sheet2 검체채취
                 * ======================================
                 */
                Sheet sheet2 = workbook.getSheet("Sheet2");
                /*상단 정보영역*/
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                String[] sheet2Info = {
                          "AD1" //품목코드
                        , "AD3" //문서번호(JQP12-01)
                        , "AD4" //문서버전(Rev.01)
                        , "F6"  //제품명
                        , "AD6" // 고객사
                        , "F7"  //제조일자
                        , "R7"  //제조번호
                        , "AD7" // 표시량
                        , "F8"  // 로트인쇄
                        , "AD8" // 검사자
                };

                for (int i = 0; i < sheet2Info.length; i++) {
                    ExcelStyleUtil.getCellRef(sheet2, sheet2Info[i]).setCellValue(infoData[i]);
                }

                Boolean isSample = sampleData != null;

                //채취일자 Q29
                ExcelStyleUtil.getCellRef(sheet2, "Q29").setCellValue((isSample && sampleData.getSampleDate() != null) ? sampleData.getSampleDate() : "");
                setSampleData(sheet2,sampleData);

                /** Sheet3 : 캡핑세기, 중량검사(에센스) **/
                Sheet sheet3 = workbook.getSheet("Sheet3");

                /** 상단 정보영역 **/
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                String[] sheet3Info = { "AD1", "AD3", "AD4", "F6", "AD6", "F7", "R7", "AD7", "F8", "AD8" };
                for (int i = 0; i < sheet3Info.length; i++) {
                    ExcelStyleUtil.getCellRef(sheet3, sheet3Info[i]).setCellValue(infoData[i]);
                }

                /** 캡핑세기(완제품) **/
                //측정범위
                ExcelStyleUtil.getCellRef(sheet3, "AD12").setCellValue(mst.getCappingRange());

                String[] lineNameList = { "I14", "M14", "Q14", "U14", "Y14" };
                //                for (int i = 0; i < lineNameList.length; i++) {
//                    String lineName = (CA515_line.get(i).getLineName() !=null && !CA515_line.get(i).getLineName().equals("")) ?
//                            CA515_line.get(i).getLineName() + " 라인" : "(     )라인";
//
//                    ExcelStyleUtil.getCellRef(sheet3, lineNameList[i]).setCellValue(lineName);
//                }
                setLineNames(sheet3, lineNameList, CA515_line);

                int idx4 = 15;
                for (QcProcDetailVo items : CA515_details) {
                    //채취시간
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet3, "E" + idx4).setCellValue(testTime);
                    //라인1
                    String line1 = (items.getLine1() != null) ? items.getLine1().toString() + " N.M" : "N.M";
                    ExcelStyleUtil.getCellRef(sheet3, "I" + idx4).setCellValue(line1);
                    //라인2
                    String line2 = (items.getLine2() != null) ? items.getLine2().toString() + " N.M" : "N.M";
                    ExcelStyleUtil.getCellRef(sheet3, "M" + idx4).setCellValue(line2);
                    //라인3
                    String line3 = (items.getLine3() != null) ? items.getLine3().toString() + " N.M" : "N.M";
                    ExcelStyleUtil.getCellRef(sheet3, "Q" + idx4).setCellValue(line3);
                    //라인4
                    String line4 = (items.getLine4() != null) ? items.getLine4().toString() + " N.M" : "N.M";
                    ExcelStyleUtil.getCellRef(sheet3, "U" + idx4).setCellValue(line4);
                    //라인5
                    String line5 = (items.getLine5() != null) ? items.getLine5().toString() + " N.M" : "N.M";
                    ExcelStyleUtil.getCellRef(sheet3, "Y" + idx4).setCellValue(line5);
                    //검사겳과
                    ExcelStyleUtil.getCellRef(sheet3, "AC" + idx4).setCellValue(getPassResult(items.getPassYn(),true));
                    idx4++;
                }

                /** 중량검사(에센스) **/
                //충전량기준
                ExcelStyleUtil.getCellRef(sheet3, "AD33").setCellValue(mst.getEssenceStd());

                String[] lineNameList2 = { "I35", "M35", "Q35", "U35", "Y35" };
                setLineNames(sheet3, lineNameList2, ES515_line);

                int idx5 = 36;
                for (QcProcDetailVo items : ES515_details) {
                    //채취시간
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet3, "E" + idx5).setCellValue(testTime);
                    //라인1
                    String line1 = (items.getLine1() != null) ? items.getLine1().toString() + " g" : "g";
                    setWeightCell(sheet3, "I"+ idx5,  items.getLine1());
                    //라인2
                    setWeightCell(sheet3, "M"+ idx5,  items.getLine2());
                    setWeightCell(sheet3, "Q"+ idx5,  items.getLine3());
                    setWeightCell(sheet3, "U"+ idx5,  items.getLine4());
                    setWeightCell(sheet3, "Y"+ idx5,  items.getLine5());
                    //검사겳과
                    ExcelStyleUtil.getCellRef(sheet3, "AC" + idx5).setCellValue(getPassResult(items.getPassYn(),false));
                    idx5++;
                }

                /*
                 * ======================================
                 * byte[] 반환
                 * ======================================
                 */
                return ExcelStyleUtil.toByteArray(workbook);

            }finally {
                workbook.close();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("엑셀파일 생성중 에러발생!", ex);
        }
    }

    private byte[] getExcelTypeD(QcProcTestVo mst) throws Exception {
        Long qcProcTestMstId = mst.getQcProcTestMstId();
        /*
         * ==========================================
         * 데이터 조회
         * ==========================================
         */
        //코팅 공정
        List<QcProcMethodVo> coatingList = qcProcTestMapper.getMethodList("QRC003", qcProcTestMstId);
        // 충전 공정
        List<QcProcMethodVo> methodList = qcProcTestMapper.getMethodList("QRC004", qcProcTestMstId);
        // 포장 공정
        List<QcProcMethodVo> packingMethods = qcProcTestMapper.getMethodList("QRC005", qcProcTestMstId);
        // 코팅 작업지시
        WorkOrderInfoVo coatingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC003");
        // 충전 작업지시
        WorkOrderInfoVo chargingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC004");
        // 포장 작업지시
        WorkOrderInfoVo packingItem = qcProcTestMapper.getProcItem(mst.getWorkBatchId(), "PRC005");
        // WE613 검사 데이터
        List<QcProcDetailVo> WE613_details = qcProcTestMapper.getDetailList("WE613", qcProcTestMstId);
        //WE613 line setting
        List<QcProcLinelVo> WE613_line = qcProcTestMapper.getLineList("WE613", qcProcTestMstId);
        // 검체채취
        QcProcSampleVo sampleData = qcProcTestMapper.getQcProcSample("S", qcProcTestMstId);

        /*
         * ==========================================
         * Excel 생성
         * ==========================================
         */
        try (
                InputStream excelStream = getClass().getResourceAsStream("/excel/qc_proc_test_type_d.xlsx")
        ) {
            if (excelStream == null) {
                throw new Exception("Excel Template을 찾을 수 없습니다. "+ "/excel/qc_proc_test_type_d.xlsx");
            }
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);

            try {
                /*
                 * ======================================
                 * 상단 기본정보
                 * ======================================
                 */
                String[] infoData = {
                        ComUtil.nvl(mst.getItemCd())
                        ,"JQP12-01"
                        ,"(Rev.03)",
                        ComUtil.nvl(mst.getItemName()),
                        ComUtil.nvl(mst.getClientName()),
                        mst.getProdDate() != null? sdf.format(mst.getProdDate()): "",
                        ComUtil.nvl(mst.getMakeNo()),
                        ComUtil.nvl(mst.getDisplayCapacity()),
                        getLotNo(mst),
                        ComUtil.nvl(mst.getTesterId())
                };
                /*
                 * ======================================
                 * Sheet1
                 * ======================================
                 */
                /** Sheet1 : 코팅작업, 충전작업, 중량검사, 겔수량검사, 포장작업 **/
                Sheet sheet1 = workbook.getSheet("Sheet1");

                /*상단 정보영역*/
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                String[] sheet1Info = { "AK1", "AK3", "AK4", "F6", "AK6", "F7", "V7", "AK7", "F8", "AK8" };

                for (int i = 0; i < 10; i++) {
                    ExcelStyleUtil.getCellRef(sheet1, sheet1Info[i]).setCellValue(infoData[i]);
                }

                /*
                 * ======================================
                 * 코팅작업
                 * ======================================
                 */
                //작업일자 (시작일시)Q11
                String coating_startTime = (coatingItem.getWorkStartTime() != null)? sdf.format(coatingItem.getWorkStartTime()) : "";
                ExcelStyleUtil.getCellRef(sheet1, "Q10").setCellValue(coating_startTime);
                //작업일자 (종료일시)X11
                String coating_endTime = (coatingItem.getWorkEndTime() != null)? sdf.format(coatingItem.getWorkEndTime()) : "";
                ExcelStyleUtil.getCellRef(sheet1, "X10").setCellValue(coating_endTime);

                int idx1 = 12;

                for (QcProcMethodVo item : coatingList){
                    ExcelStyleUtil.getCellRef(sheet1, "C" + idx1).setCellValue(item.getTestMethod());
                    ExcelStyleUtil.getCellRef(sheet1, "O" + idx1).setCellValue(item.getTestItem());
                    ExcelStyleUtil.getCellRef(sheet1,"AD" + idx1).setCellValue(getMethodResult(item.getTestResult()));
                    idx1++;
                }
                /*
                 * ======================================
                 * 충전작업
                 * ======================================
                 */
                String chargingStartTime = chargingItem != null
                        && chargingItem.getWorkStartTime() != null
                        ? sdf.format(chargingItem.getWorkStartTime()): "";

                String chargingEndTime =
                        chargingItem != null&& chargingItem.getWorkEndTime() != null
                                ? sdf.format(chargingItem.getWorkEndTime()): "";

                ExcelStyleUtil.getCellRef(sheet1,"Q18").setCellValue(chargingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X18").setCellValue(chargingEndTime);

                int idx2 = 20;

                for (QcProcMethodVo item : methodList ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + idx2).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + idx2).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"Z" + idx2).setCellValue(getMethodResult(item.getTestResult()));
                    idx2++;
                }

                //충전지시량 AD27
                ExcelStyleUtil.getCellRef(sheet1, "AH28").setCellValue(ComUtil.nvl(mst.getChargingCnt()));

                String[] lineNameList = { "I30", "M30", "Q30", "U30", "Y30", "AC30" };

                for (int i = 0; i < lineNameList.length; i++) {
                    String lineName = (WE613_line.get(i).getLineName() !=null && !WE613_line.get(i).getLineName().equals("")) ?
                            WE613_line.get(i).getLineName() + " 라인" : "(     )라인";
                    ExcelStyleUtil.getCellRef(sheet1, lineNameList[i]).setCellValue(lineName);
                }

                int idx3 = 31;
                for (QcProcDetailVo items : WE613_details) {
                    String testTime = (items.getTestTime() != null && !items.getTestTime().equals(""))? items.getTestTime() : ":";
                    ExcelStyleUtil.getCellRef(sheet1, "E" + idx3).setCellValue(testTime);
                    setWeightCell(sheet1,"I" + idx3, items.getLine1());
                    setWeightCell(sheet1,"M" + idx3, items.getLine2());
                    setWeightCell(sheet1,"Q" + idx3, items.getLine3());
                    setWeightCell(sheet1,"U" + idx3, items.getLine4());
                    setWeightCell(sheet1,"Y" + idx3, items.getLine5());
                    setWeightCell(sheet1,"AC" + idx3, items.getLine6());
                    ExcelStyleUtil.getCellRef(sheet1,"AG" + idx3).setCellValue(getPassResult(items.getPassYn(),true));
                    idx3++;
                }
                /*
                 * ======================================
                 * 포장작업
                 * ======================================
                 */
                String packingStartTime = packingItem != null&& packingItem.getWorkStartTime() != null
                        ? sdf.format(packingItem.getWorkStartTime()): "";
                String packingEndTime =packingItem != null
                        && packingItem.getWorkEndTime() != null
                        ? sdf.format(packingItem.getWorkEndTime()): "";
                ExcelStyleUtil.getCellRef(sheet1,"Q46").setCellValue(packingStartTime);
                ExcelStyleUtil.getCellRef(sheet1,"X46").setCellValue(packingEndTime);

                int idx4 = 48;

                for (QcProcMethodVo item : packingMethods ) {
                    ExcelStyleUtil.getCellRef(sheet1,"C" + idx4).setCellValue(ComUtil.nvl(item.getTestMethod()));
                    ExcelStyleUtil.getCellRef(sheet1,"O" + idx4).setCellValue(ComUtil.nvl(item.getTestItem()));
                    ExcelStyleUtil.getCellRef(sheet1,"AD" + idx4).setCellValue(getMethodResult(item.getTestResult()));
                    idx4++;
                }

                //비고 A58
                ExcelStyleUtil.getCellRef(sheet1, "A56").setCellValue(mst.getEtc());

                /*
                 * ======================================
                 * Sheet2 검체채취
                 * ======================================
                 */
                Sheet sheet2 = workbook.getSheet("Sheet2");
                /*상단 정보영역*/
                //품목코드, 문서번호(JQP12-01), 문서버전(Rev.01), 제품명, 고객사, 제조일자, 제조번호, 표시량, 로트인쇄, 검사자
                String[] sheet2Info = { "AD1", "AD3", "AD4", "F6", "AD6", "F7", "R7", "AD7", "F8", "AD8" };

                for (int i = 0; i < sheet1Info.length; i++) {
                    ExcelStyleUtil.getCellRef(sheet2, sheet2Info[i]).setCellValue(infoData[i]);
                }

                Boolean isSample = sampleData != null;

                //채취일자 Q29
                ExcelStyleUtil.getCellRef(sheet2, "Q29").setCellValue((isSample && sampleData.getSampleDate() != null) ? sampleData.getSampleDate() : "");
                setSampleData(sheet2, sampleData);
                /*
                 * ======================================
                 * byte[] 반환
                 * ======================================
                 */
                return ExcelStyleUtil.toByteArray(workbook);

            }finally {
                workbook.close();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("엑셀파일 생성중 에러발생!", ex);
        }
    }






    /* =========================================================
     * LOT 번호
     * ========================================================= */
    private String getLotNo(QcProcTestVo master) {
        if ( master.getLotNo2() != null&& !master.getLotNo2().isBlank()&& !"/".equals(master.getLotNo2())) {
            return master.getLotNo2();
        }
        return master.getLotNo() != null? master.getLotNo(): "";
    }

    /* =========================================================
     * QRC 검사결과
     * ========================================================= */
    private String getMethodResult(String testResult) {
        if ("Y".equals(testResult)) {
            return "■ 양호   □ 양호하지않음";
        }
        if ("N".equals(testResult)) {
            return "□ 양호   ■ 양호하지않음";
        }
        return "□ 양호   □ 양호하지않음";
    }

    /* =========================================================
     * 적합 / 부적합
     * ========================================================= */
    private String getPassResult(String passYn,boolean hasReason) {
        String reason =hasReason? "(           )": "";

        if ("Y".equals(passYn)) {
            return "■ 적합   □ 부적합"+ reason;
        }
        if ("N".equals(passYn)) {
            return "□ 적합   ■ 부적합"+ reason;
        }

        return "□ 적합   □ 부적합"+ reason;
    }

    /* =========================================================
     * 시간
     * ========================================================= */
    private String emptyTime(String testTime) {
        return testTime == null|| testTime.isBlank()? ":": testTime;
    }

    /* =========================================================
     * 중량 Cell
     * ========================================================= */
    private void setWeightCell(Sheet sheet,String address,BigDecimal value) {
        String text =value != null? value.toPlainString()+ " g": "g";

        ExcelStyleUtil.getCellRef(sheet,address).setCellValue(text);
    }

    private void setLineNames(Sheet sheet,String[] cellAddresses, List<QcProcLinelVo> lineList) {
        for (int i = 0; i < cellAddresses.length; i++) {
            /*
             * i = 0 -> orderDist 1
             * i = 1 -> orderDist 2
             * ...
             */
            int orderDist = i + 1;

            QcProcLinelVo line =
                    lineList == null
                            ? null
                            : lineList
                            .stream()
                            .filter(
                                    item ->
                                            item.getOrderDist() != null
                                                    && item.getOrderDist()
                                                    == orderDist
                            )
                            .findFirst()
                            .orElse(null);
            String lineName =
                    line != null
                            && line.getLineName() != null
                            && !line.getLineName().isBlank()
                            ? line.getLineName()
                            : "(     )라인";

            ExcelStyleUtil.getCellRef(sheet,cellAddresses[i]).setCellValue(lineName);
        }
    }

    private void setSampleData(Sheet sheet, QcProcSampleVo sample) {
        boolean exists = sample != null;
        /*
         * 채취일자
         */
        ExcelStyleUtil.getCellRef(sheet,"Q29")
                .setCellValue(
                        exists
                                && sample.getSampleDate() != null
                                ? sample.getSampleDate()
                                : ""
                );

        int[] qtys = {
                exists && sample.getQty1() != null ? sample.getQty1(): 0,
                exists && sample.getQty2() != null? sample.getQty2(): 0,
                exists && sample.getQty3() != null? sample.getQty3(): 0,
                exists && sample.getQty4() != null? sample.getQty4(): 0,
                exists && sample.getQty5() != null? sample.getQty5(): 0,
                exists && sample.getQty6() != null? sample.getQty6(): 0,
                exists && sample.getQty7() != null? sample.getQty7(): 0,
                exists && sample.getQty8() != null? sample.getQty8(): 0,
                exists && sample.getQty9() != null? sample.getQty9(): 0
        };

        String[] addresses = {"E37", "G37", "I37", "K37", "M37", "O37","Q37", "W37", "AC37"};

        int total = 0;

        for (int i = 0; i < addresses.length; i++ ) {
            ExcelStyleUtil.getCellRef(sheet,addresses[i]).setCellValue(qtys[i]);
            total += qtys[i];
        }

        ExcelStyleUtil.getCellRef(sheet,"AD30").setCellValue(total);
    }

}
