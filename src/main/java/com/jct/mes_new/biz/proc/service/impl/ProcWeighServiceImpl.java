package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.PurchaseVo;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.vo.TranLedgerVo;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcWeighServiceImpl implements ProcWeighService {

    private final ProcWeighMapper procWeighMapper;
    private final ProcCommonMapper procCommonMapper;
    private final TranMapper tranMapper;
    private final WorkOrderMapper workOrderMapper;

    /**
     * 칭량 상세 조회
     * @param vo
     * @return
     */
    public WeighInfoVo getWeighInfo(ProcWeighVo vo){
        WeighInfoVo info = new WeighInfoVo();

        info.setWorkOrderInfo(workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId()));
        List<ProcWeighBomVo> recipeList = null;

        if (procWeighMapper.checkWeighCnt(vo.getWorkProcId()) > 0 ){
            recipeList = procWeighMapper.getRealBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }else{
            recipeList = procWeighMapper.getBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }
        info.setWeightBomList(recipeList);

        return info;
    }

    /**
     * 칭량 작업 시작
     * @param vo
     * @return
     */
    public String startProcWeigh(ProcWeighVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        //배치 상태 업데이트
        ProcCommonVo comVo = new ProcCommonVo();
        comVo.setWorkBatchId(vo.getWorkBatchId());
        comVo.setBatchStatus(vo.getBatchStatus());
        comVo.setUserId(userId);
        procCommonMapper.updateBatchStatus(comVo);

        // 작업지시 상태 업데이트 (작업상태, 배치상태, 작업처, 작업일자, 작업시간
        if (procWeighMapper.startProcWeigh(vo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        List<ProcWeighBomVo>  recipeList = procWeighMapper.getBomWeighList(vo.getWorkProcId(), vo.getItemCd());

        for ( ProcWeighBomVo item : recipeList ){
            item.setUserId(userId);
            item.setWorkProcId(vo.getWorkProcId());
            item.setWorkBatchId(vo.getWorkBatchId());

            if ( procWeighMapper.insertWeighRecipe(item) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        return "칭량작업을 시작할수 있습니다.";
    }

    /**
     * 칭량량 조회
     * @param vo
     * @return
     */
    public List<ProcWeighVo> getStockTestNoList(ProcWeighVo vo){
        return procWeighMapper.getStockTestNoList(vo);
    }

    /**
     * 칭량량 등록  (재고용)
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String saveWeighList(WeighInvInfo vo){
        ProcWeighVo mst = vo.getWeighInfo();
        List<ProcWeighVo> weighList = vo.getWeighList();
        List<Long> deleteWeighItems = vo.getDeleteWeighItems();

        String userId = UserUtil.getUserId();

        if (deleteWeighItems != null && !deleteWeighItems.isEmpty()) {
            procWeighMapper.deleteWeighList(mst.getWorkProcId(), deleteWeighItems);
        }

        if (weighList != null && !weighList.isEmpty()) {
            for (ProcWeighVo item : weighList) {
                item.setWeighId(mst.getWeighId());
                item.setUserId(userId);

                if ( item.getWeighInvId() == null ){
                    if (procWeighMapper.insertWeighInvItem(item) <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }else{
                    if (procWeighMapper.updateWeighInvItem(item) <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                }
            }
        }
        //칭량 bom 정보 업데이트
        ProcWeighBomVo weigh = new ProcWeighBomVo();
        weigh.setWeighId(mst.getWeighId());
        weigh.setWeighQty(mst.getWeighQty());
        weigh.setTestNo(mst.getTestNo());
        weigh.setWeighYn("Y");
        procWeighMapper.updateWeighInfo(weigh);

        return "칭량정보를 저장했습니다.";
    }

    /**
     * 칭량 정보 및 리스트 저장
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public Long saveWeighInfo(WeighInfoVo vo) {
        WorkOrderInfoVo mst = vo.getWorkOrderInfo();
        List<ProcWeighBomVo> recipeList = vo.getWeightBomList();
        String userId = UserUtil.getUserId();

        if (procWeighMapper.updateProcWeigh(mst) <=0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        int cntWeigh = procWeighMapper.checkWeighCnt(mst.getWorkProcId());

        for(ProcWeighBomVo item : recipeList ){
            item.setWorkProcId(mst.getWorkProcId());
            item.setWorkBatchId(mst.getWorkBatchId());
            item.setUserId(userId);

            if ("Y".equals(item.getWeighYn()) ) {
                if (procWeighMapper.updateWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return mst.getWorkProcId();
    }

    public String updateBagWeigh(ProcWeighVo vo){
        procWeighMapper.updateBagWeigh(vo);
        return "저장되었습니다.";
    }

    /**
     * 칭량 완료
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public Long completeWeight(ProcWeighVo vo){
        String userId = UserUtil.getUserId();

        //재고 마스터
        WorkOrderInfoVo mst = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());
        TranVo invMst = new TranVo();

        String storageCd = "";
        if ( "A001".equals(mst.getAreaCd())){
            storageCd = "WS005";
        }else if ( "A002".equals(mst.getAreaCd())){
            storageCd = "WA005";
        }else{
            storageCd = "WS005";
        }

        invMst.setTranDate(LocalDate.now());
        invMst.setTranTypeCd("E");
        invMst.setAreaCd(mst.getAreaCd());
        invMst.setSrcStorageCd(mst.getStorageCd() );
        invMst.setTarStorageCd(storageCd);
        invMst.setManagerId(mst.getManagerId());
        invMst.setEndYn("N");
        invMst.setTranStatus("E");
        invMst.setPoNo(mst.getPoNo());
        invMst.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //재고 상세
        List<ProcWeighVo> weighInvList = procWeighMapper.getWeighInvInfo(vo.getWorkProcId());

        for(ProcWeighVo item : weighInvList) {
            TranItemVo tranItemVo = new TranItemVo();

            tranItemVo.setTranId(invMst.getTranId());
            tranItemVo.setItemTypeCd(item.getItemTypeCd());
            tranItemVo.setItemCd(item.getItemCd());
            tranItemVo.setItemName(item.getItemName());
            tranItemVo.setLotNo(item.getLotNo());
            tranItemVo.setTestNo(item.getTestNo());
            tranItemVo.setExpiryDate(item.getExpiryDate());
            tranItemVo.setQty(item.getWeighQty());
            tranItemVo.setWeighInvId(item.getWeighInvId());
            tranItemVo.setUserId(userId);

            if ( tranMapper.insertTranItem(tranItemVo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        vo.setOutTranId(invMst.getTranId() );
        vo.setUserId(userId);
        //작업지시 업데이트 (공정)
        if( procWeighMapper.updateWeighProcComplete(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //작업지시 업데이트 (배치)
        ProcCommonVo comVo = new ProcCommonVo();
        comVo.setWorkProcId(vo.getWorkProcId());
        comVo.setWorkBatchId(vo.getWorkBatchId());
        comVo.setBatchStatus(vo.getBatchStatus());
        comVo.setUserId(userId);
        procCommonMapper.updateBatchStatus(comVo);
        return vo.getWorkProcId();
    }

    public List<TranLedgerVo> getWeighCloseList(TranLedgerVo vo){
        return procWeighMapper.getWeighCloseList(vo);
    }

    public List<TranLedgerVo> getItemCloseList(TranLedgerVo vo){
        return procWeighMapper.getItemCloseList(vo);
    }


    public byte[] downloadWeighProc(ProcWeighVo vo) {

        WorkOrderInfoVo workOrderInfo =
                workOrderMapper.getWorkOrderProcInfo(
                        vo.getProcCd(),
                        vo.getWorkProcId()
                );

        if (workOrderInfo == null) {
            throw new BusinessException("작업지시 정보를 찾을 수 없습니다.");
        }

        List<ProcWeighBomVo> weighList =
                procWeighMapper.getRealBomWeighList(
                        vo.getWorkProcId(),
                        vo.getItemCd()
                );

        if (weighList == null) {
            weighList = new ArrayList<>();
        }

        int size = weighList.size();

        // ==========================================
        // 1. 템플릿 및 합계 행 설정
        // ==========================================
        String templateName;
        int sumRow;

        if (size <= 25) {
            templateName = "/excel/prod_weigh_record_page1.xlsx";
            sumRow = 36;
        } else if (size <= 56) {
            templateName = "/excel/prod_weigh_record_page2.xlsx";
            sumRow = 67;
        } else if (size <= 87) {
            templateName = "/excel/prod_weigh_record_page3.xlsx";
            sumRow = 98;
        } else {
            throw new BusinessException(
                    "칭량 항목이 최대 출력 가능 건수(87건)를 초과했습니다."
            );
        }

        // ==========================================
        // 2. 엑셀 템플릿 로드
        // ==========================================
        try (InputStream excelStream =
                     getClass().getResourceAsStream(templateName)) {

            if (excelStream == null) {
                throw new BusinessException(
                        "엑셀 템플릿을 찾을 수 없습니다: " + templateName
                );
            }

            try (Workbook workbook =
                         ExcelStyleUtil.createWorkbook(excelStream)) {

                Sheet sheet = workbook.getSheet("Sheet1");

                if (sheet == null) {
                    throw new BusinessException(
                            "엑셀 템플릿에 Sheet1 시트가 없습니다."
                    );
                }

                // ==========================================
                // 3. 작업지시 기본정보
                // ==========================================
                ExcelStyleUtil.getCellRef(sheet, "U1")
                        .setCellValue(
                                safeString(workOrderInfo.getItemCd())
                        );

                ExcelStyleUtil.getCellRef(sheet, "F2")
                        .setCellValue(
                                safeString(workOrderInfo.getItemName())
                        );

                ExcelStyleUtil.getCellRef(sheet, "E5")
                        .setCellValue(
                                safeString(workOrderInfo.getClientName())
                        );

                if (workOrderInfo.getProdDate() != null) {
                    String prodDateFormat =
                            workOrderInfo.getProdDate().format(
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                            );

                    ExcelStyleUtil.getCellRef(sheet, "Q5")
                            .setCellValue(prodDateFormat);
                }

                double orderQty =
                        workOrderInfo.getOrderQty() != null
                                ? workOrderInfo.getOrderQty().doubleValue()
                                : 0.0;

                ExcelStyleUtil.getCellRef(sheet, "E6")
                        .setCellValue(orderQty);

                ExcelStyleUtil.getCellRef(sheet, "Q6")
                        .setCellValue(
                                safeString(workOrderInfo.getMakeNo())
                        );

                // ==========================================
                // 4. 칭량 상세정보
                // ==========================================
                int rowNo = 11;

                List<String> weighMembers = new ArrayList<>();
                List<String> weighConfirmMembers = new ArrayList<>();

                for (ProcWeighBomVo item : weighList) {

                    // 품목코드
                    ExcelStyleUtil.getCellRef(sheet, "B" + rowNo)
                            .setCellValue(
                                    safeString(item.getItemCd())
                            );

                    // 품목명
                    ExcelStyleUtil.getCellRef(sheet, "E" + rowNo)
                            .setCellValue(
                                    safeString(item.getItemName())
                            );

                    // 상 구분
                    ExcelStyleUtil.getCellRef(sheet, "K" + rowNo)
                            .setCellValue(
                                    safeString(item.getPhase())
                            );

                    // 칭량지시량
                    double reqQty =
                            item.getOrderQty() != null
                                    ? item.getOrderQty().doubleValue()
                                    : 0.0;

                    ExcelStyleUtil.getCellRef(sheet, "N" + rowNo)
                            .setCellValue(reqQty);

                    // 기준량(함량 %)
                    double contentRatio =
                            orderQty != 0
                                    ? reqQty / orderQty * 100.0
                                    : 0.0;

                    ExcelStyleUtil.getCellRef(sheet, "L" + rowNo)
                            .setCellValue(contentRatio);

                    // 시험번호
                    ExcelStyleUtil.getCellRef(sheet, "Q" + rowNo)
                            .setCellValue(
                                    safeString(item.getTestNoJoin())
                            );

                    // 실칭량량
                    double weighQty =
                            item.getWeighQty() != null
                                    ? item.getWeighQty().doubleValue()
                                    : 0.0;

                    ExcelStyleUtil.getCellRef(sheet, "T" + rowNo)
                            .setCellValue(weighQty);

                    // 작업자
                    ExcelStyleUtil.getCellRef(sheet, "W" + rowNo)
                            .setCellValue(
                                    safeString(item.getWeigher())
                            );

                    // 작업자 목록
                    if (item.getWeigher() != null
                            && !item.getWeigher().isBlank()) {

                        weighMembers.add(item.getWeigher());
                    }

                    // 확인자 목록
                    if (item.getConfirmer() != null
                            && !item.getConfirmer().isBlank()) {

                        weighConfirmMembers.add(item.getConfirmer());
                    }

                    rowNo++;
                }

                // ==========================================
                // 5. 작업자 / 확인자
                // ==========================================
                ExcelStyleUtil.getCellRef(sheet, "E7")
                        .setCellValue(
                                safeString(distinctAndJoining(weighMembers))
                        );

                ExcelStyleUtil.getCellRef(sheet, "Q7")
                        .setCellValue(
                                safeString(distinctAndJoining(weighConfirmMembers))
                        );

                // ==========================================
                // 6. 합계 수식
                // ==========================================
                int endRow = sumRow - 1;

                // 함량(%) 합계
                ExcelStyleUtil.getCellRef(sheet, "L" + sumRow)
                        .setCellFormula(
                                "SUM(L11:L" + endRow + ")"
                        );

                // 칭량지시량 합계
                ExcelStyleUtil.getCellRef(sheet, "N" + sumRow)
                        .setCellFormula(
                                "SUM(N11:N" + endRow + ")"
                        );

                // 실칭량량 합계
                ExcelStyleUtil.getCellRef(sheet, "T" + sumRow)
                        .setCellFormula(
                                "SUM(T11:T" + endRow + ")"
                        );

                // ==========================================
                // 7. 수식 계산
                // ==========================================
                FormulaEvaluator evaluator =
                        workbook.getCreationHelper()
                                .createFormulaEvaluator();

                // 수식 결과를 계산하고 캐시에 저장
                evaluator.evaluateAll();

                // Excel에서 열 때 다시 계산
                workbook.setForceFormulaRecalculation(true);

                // ==========================================
                // 8. 엑셀 파일 반환
                // ==========================================
                return ExcelStyleUtil.toByteArray(workbook);
            }

        } catch (BusinessException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new BusinessException(
                    "칭량작업 엑셀파일 생성 중 오류가 발생했습니다: "
                            + ex.getMessage()
            );
        }
    }

    // null 문자열 처리
    private String safeString(String value) {
        return value != null ? value : "";
    }


    public String distinctAndJoining (List<String> stringList) {
        return stringList.stream().distinct().collect(Collectors.joining(", "));
    }

}
