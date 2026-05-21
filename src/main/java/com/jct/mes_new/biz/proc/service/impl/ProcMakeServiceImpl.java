package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.lab.mapper.BomMapper;
import com.jct.mes_new.biz.lab.vo.BomProcVo;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcMakeMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcMakeService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.purchase.mapper.PurchaseMapper;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcProcTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcMakeServiceImpl implements ProcMakeService {

    private final ProcMakeMapper procMakeMapper;
    private final ProcCommonMapper procCommonMapper;
    private final ItemTestMapper itemTestMapper;
    private final QcTestMapper qcTestMapper;
    private final WorkOrderMapper workOrderMapper;
    private final BomMapper bomMapper;
    private final TranMapper tranMapper;
    private final ProcWeighMapper procWeighMapper;

    public List<ProcMakeVo> getMatList(ProcMakeVo vo){
        return procMakeMapper.getMatList(vo);
    }

    /**
     * 제조 상세 조회
     * @param vo
     * @return
     */
    public MakeInfoVo getMakeInfo(ProcMakeVo vo){
        MakeInfoVo info = new MakeInfoVo();
        MakeEtcVo makeEtcVo = new MakeEtcVo();
        String procCd = "PRC001";
        //제조량은 칭량정보를 가져오기 때문에   workProcId를 칭량으로 가져와야함
        Long workProcId = procCommonMapper.getWorkProcId(vo.getWorkBatchId(), procCd);
        info.setProcMake(procMakeMapper.getMakeHeadInfo(vo.getWorkBatchId()));
        //제조량은 칭량정보를 가져오기 때문에 workProcId를 칭량으로 가져와야함
        info.setMakeBomList(procMakeMapper.getRealBomMakeList(workProcId, vo.getItemCd()));
        //bom  정보
        List<BomProcVo> bomProcList = bomMapper.getBomProcInfo(vo.getItemCd());

        AtomicReference<String> prevPhase = new AtomicReference<>("");
        //bom정보 가져오기
        String result = bomProcList.stream()
                .filter(v -> v.getPhase() != null)
                .map(v -> {
                    String currentPhase = v.getPhase();

                    String prefix = "";
                    if (!currentPhase.equals(prevPhase.get()) && !prevPhase.get().isEmpty()) {
                        prefix = "\n";
                    }
                    prevPhase.set(currentPhase);

                    String procType = Optional.ofNullable(v.getProcType()).orElse("");
                    String matProc  = Optional.ofNullable(v.getMatProc()).orElse("");

                    String phaseLine = currentPhase + (procType.isEmpty() ? "" : ". " + procType);

                    return prefix + phaseLine + "\n" + matProc;
                })
                .collect(Collectors.joining("\n"));

        String result2 = bomProcList.stream()
                .map(v -> {
                    List<String> list = Stream.of(
                                    v.getH() != null ? "H : " + v.getH() : null,
                                    v.getP() != null ? "P : " + v.getP() : null,
                                    v.getD1() != null ? "D1 : " + v.getD1() : null,
                                    v.getD2() != null ? "D2 : " + v.getD2() : null,
                                    v.getT() != null ? "T : " + v.getT() : null,
                                    v.getM() != null ? "M : " + v.getM() : null,
                                    v.getP2() != null ? "P2 : " + v.getP2() : null,
                                    v.getRt() != null ? "RT : " + v.getRt() : null
                            )
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    // 2개씩 묶기
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < list.size(); i += 2) {
                        if (i + 1 < list.size()) {
                            sb.append(list.get(i)).append("    ").append(list.get(i + 1));
                        } else {
                            sb.append(list.get(i));
                        }
                        sb.append("\n");
                    }

                    return sb.toString().trim();
                })
                .collect(Collectors.joining("\n\n"));
        makeEtcVo = procMakeMapper.getMakeEtcInfo(vo.getWorkProcId());
        makeEtcVo.setMatProc(result);
        makeEtcVo.setManipulate(result2);
        makeEtcVo.setBomVer(bomProcList.get(0).getBomVer());
        makeEtcVo.setNote(bomProcList.get(0).getNote());
        makeEtcVo.setSignificant(bomProcList.get(0).getSignificant());

        info.setMakeEtcInfo(makeEtcVo);

        return info;
    }

    /**
     * 제조 작업 시작
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String startProcMake(ProcMakeVo vo){
        String userId = UserUtil.getUserId();

        //testNo 추출
        String prefix = CodeUtil.createTestNo(LocalDate.now(), vo.getAreaCd(), "M3");
        Integer nextSeq = itemTestMapper.getNextTestNoSeq(prefix);
        String testNo = prefix + String.format("%03d", nextSeq);

        //work_proc업데이트
        vo.setUserId(userId);
        vo.setTestNo(testNo);
        if(procMakeMapper.startProcMake(vo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //work_batch 업데이트
        ProcCommonVo procCommonVo = new ProcCommonVo();
        procCommonVo.setWorkBatchId(vo.getWorkBatchId());
        procCommonVo.setBatchStatus(vo.getBatchStatus());
        procCommonVo.setUserId(userId);
        if(procCommonMapper.updateBatchStatus(procCommonVo) <= 0){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        ProcMakeVo procMakeVo = procMakeMapper.getMakeHeadInfo(vo.getWorkBatchId());

        LocalDate shelfLife = LocalDate.now().plusDays(365 * 2);
        LocalDate expiryDate = LocalDate.now().plusDays(365 * 2);

        //시험번호별 내역 등록
        ItemTestVo itemTestVo = new ItemTestVo();
        itemTestVo.setTestNo(testNo);
        itemTestVo.setCreateDate(LocalDate.now());
        itemTestVo.setAreaCd(procMakeVo.getAreaCd());
        itemTestVo.setItemTypeCd("M3");
        itemTestVo.setSeq(1);
        itemTestVo.setItemCd(procMakeVo.getItemCd());
        itemTestVo.setItemName(procMakeVo.getItemName());
        itemTestVo.setMakeNo(procMakeVo.getMakeNo());
        itemTestVo.setLotNo(procMakeVo.getLotNo());
        itemTestVo.setCustomerCd(procMakeVo.getClientId());
        itemTestVo.setQty(BigDecimal.ZERO);
        itemTestVo.setExpiryDate(expiryDate);
        itemTestVo.setShelfLife(shelfLife);
        itemTestVo.setTestState("REQ");
        itemTestVo.setPassState("REQ");
        itemTestVo.setUserId(userId);

        if ( itemTestMapper.insertItemTestNo(itemTestVo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //품질검사 등록
        QcTestVo qcTestVo = new QcTestVo();
        qcTestVo.setTestNo(testNo);
        qcTestVo.setReqDate(LocalDate.now());
        qcTestVo.setRetestYn("N");
        qcTestVo.setAreaCd(procMakeVo.getAreaCd());
        qcTestVo.setStorageCd(procMakeVo.getStorageCd());
        qcTestVo.setReqTesterId(userId);
        qcTestVo.setReqQty(BigDecimal.ZERO);

        if(qcTestMapper.insertSingleQcTest(qcTestVo, userId) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //공정시간  여과포 신규 생성
        MakeEtcVo makeEtcVo = new MakeEtcVo();
        makeEtcVo.setWorkProcId(vo.getWorkProcId());
        makeEtcVo.setUserId(userId);
        if( procMakeMapper.insertMakeEtcInfo(makeEtcVo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return "제조작업 시작하세요";
    }

    @Transactional(rollbackFor = BusinessException.class)
    public Long completeMake(ProcMakeVo vo){
        String userId = UserUtil.getUserId();

        //재고 마스터
        ProcMakeVo mst = procMakeMapper.getMakeHeadInfo(vo.getWorkBatchId());
        TranVo invMst = new TranVo();
        String fromStorage = "";
        if ( "A001".equals(mst.getAreaCd()) ){
            fromStorage = "WS005";
        }else if ( "A002".equals(mst.getAreaCd()) ){
            fromStorage = "WA005";
        }else{
            fromStorage = "WS005";
        }

        invMst.setTranDate(LocalDate.now());
        invMst.setTranTypeCd("B");
        invMst.setAreaCd(mst.getAreaCd());
        invMst.setFromStorageCd(mst.getStorageCd());
        invMst.setToStorageCd(fromStorage);
        invMst.setManagerId(mst.getManagerId());
        invMst.setEndYn("Y");
        invMst.setTranStatus("C");
        invMst.setPoNo(mst.getPoNo());
        invMst.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        if ( itemTestMapper.updateQty(vo.getTestNo(), mst.getProdQty(), userId) <=0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        ItemTestVo item = itemTestMapper.getItemTestNoInfo(vo.getTestNo());

        //재고 상세
        TranItemVo tranItemVo = new TranItemVo();

        tranItemVo.setTranId(invMst.getTranId());
        tranItemVo.setItemTypeCd("M3");
        tranItemVo.setItemCd(item.getItemCd());
        tranItemVo.setItemName(item.getItemName());
        tranItemVo.setLotNo(item.getLotNo());
        tranItemVo.setTestNo(item.getTestNo());
        tranItemVo.setExpiryDate(item.getExpiryDate());
        tranItemVo.setQty(item.getQty());
        tranItemVo.setUserId(userId);

        if ( tranMapper.insertTranItem(tranItemVo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        vo.setTranId(invMst.getTranId() );
        vo.setUserId(userId);
        //작업지시 업데이트 (공정)
        if( procMakeMapper.updateMakeProcComplete(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //작업지시 업데이트 (배치)
        ProcCommonVo comVo = new ProcCommonVo();
        comVo.setWorkProcId(vo.getWorkProcId());
        comVo.setWorkBatchId(vo.getWorkBatchId());
        comVo.setBatchStatus(vo.getBatchStatus());
        comVo.setUserId(userId);
        if ( procCommonMapper.updateBatchStatus(comVo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return vo.getWorkProcId();
    }

    public Long getWeighQty(Long weighId){
        return procMakeMapper.getWeighQty(weighId);
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String insertRowMake(ProcWeighBomVo vo){
        String userId = UserUtil.getUserId();
        return "저장되었습니다.";
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveMakeInfo(MakeInfoVo vo){
        String userId = UserUtil.getUserId();
        ProcMakeVo mst = vo.getProcMake();
        List<ProcWeighBomVo> makeBomList = vo.getMakeBomList();
        MakeEtcVo makeEtcInfo = vo.getMakeEtcInfo();

        //마스터 업데이트
        mst.setUserId(userId);
        if (procMakeMapper.updateProdQty(mst) <=0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //공정 업데이트
        makeEtcInfo.setUserId(userId);
        if ( procMakeMapper.updateMakeEtc(makeEtcInfo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //칭량 업데이트
        for(ProcWeighBomVo item : makeBomList ){
            item.setUserId(userId);
            if (procMakeMapper.updateMakeRecipe(item) <=0  ){
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        }

        return "저장되었습니다.";
    }


    public byte[] downloadMatProc(ProcMakeVo vo) {
        WorkOrderInfoVo workOrderInfoVo = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());
        MakeEtcVo makeEtcVo = procMakeMapper.getMakeEtcInfo(vo.getWorkProcId());
        List<BomProcVo> bomProcList = bomMapper.getBomProcInfo(vo.getItemCd());
        Long workProcId = null;
        List<ProcWeighBomVo> weighList = new ArrayList<>();

        if (workOrderInfoVo != null) {
            workProcId = procCommonMapper.getWorkProcId(workOrderInfoVo.getWorkBatchId(),"PRC001");
        }

        if (workProcId != null) {
            weighList = procMakeMapper.getRealBomMakeList(workProcId, vo.getItemCd());
        }

        if (bomProcList == null) {
            bomProcList = new ArrayList<>();
        }

        if (weighList == null) {
            weighList = new ArrayList<>();
        }

        try {
            InputStream excelStream = getClass().getResourceAsStream("/excel/mat_proc_template.xlsx");
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);
            Sheet sheet = workbook.getSheet("Sheet1");

            int START_ROW = 8;
            int LAST_DATA_ROW = 44;
            int TOTAL_ROW = 45;
            // =========================
            // 상단 정보
            // =========================
            setCellByCol(sheet, 5, "D",workOrderInfoVo == null ? null : workOrderInfoVo.getClientName());
            setCellByCol(sheet, 5, "M", workOrderInfoVo == null ? null : workOrderInfoVo.getMakeNo());
            setCellByCol(sheet, 5, "V",workOrderInfoVo == null ? null : workOrderInfoVo.getWorkEquipmentCd());
            setCellByCol(sheet, 5, "AD",makeEtcVo == null ? null : makeEtcVo.getMaker());
            setCellByCol(sheet, 5, "AL",workOrderInfoVo == null ? null : workOrderInfoVo.getProdDate());
            // 코드번호
            setCellByCol(sheet, 0, "AK", vo.getItemCd());
            // 제정일자
            setCellByCol(sheet, 2, "AK", makeEtcVo == null ? null : makeEtcVo.getBomVer());
            setCellByCol(sheet, 2, "I", vo.getItemName());
            // =========================
            // BOM Map
            // =========================
            Map<String, BomProcVo> bomProcMap = new HashMap<>();

            for (BomProcVo bomProc : bomProcList) {
                if (bomProc == null) continue;
                String gbn = bomProc.getPhase();
                if (gbn != null && !gbn.isBlank()) {
                    bomProcMap.put(gbn, bomProc);
                }
            }
            // =========================
            // 행 추가
            // =========================
            int defaultRowCount = LAST_DATA_ROW - START_ROW + 1;
            int extraRowCount = Math.max(0, weighList.size() - defaultRowCount);

            if (extraRowCount > 0) {
                sheet.shiftRows(
                        TOTAL_ROW,
                        sheet.getLastRowNum(),
                        extraRowCount,
                        true,
                        false
                );
                for (int i = 0; i < extraRowCount; i++) {
                    int newRowNum = LAST_DATA_ROW + 1 + i;
                    copyRow(workbook, sheet, LAST_DATA_ROW, newRowNum);
                }
            }
            // =========================
            // 원료 리스트
            // =========================
            BigDecimal totalMakeQty = BigDecimal.ZERO;
            for (int i = 0; i < weighList.size(); i++) {
                ProcWeighBomVo weigh = weighList.get(i);
                if (weigh == null) continue;
                int rowNum = START_ROW + i;
                String gbn = weigh.getPhase();
                // 번호
                setCellByCol(sheet, rowNum, "A",String.format("%03d", i + 1));
                // 원료코드
                setCellByCol(sheet, rowNum, "C",weigh.getItemCd());
                // 원료명칭
                setCellByCol(sheet, rowNum, "G",weigh.getItemName());
                // 구분
                setCellByCol(sheet, rowNum, "L",gbn);
                // 투입량
                setCellByCol(sheet, rowNum, "N",weigh.getOrderQty());
                // 실투입량
                setCellByCol(sheet, rowNum, "Q",weigh.getMakeQty());
                // 작업자확인
                setCellByCol(sheet, rowNum, "R",weigh.getMaker());

                if (weigh.getMakeQty() != null) {
                    totalMakeQty = totalMakeQty.add(weigh.getMakeQty());
                }
                BomProcVo bomProc = bomProcMap.get(gbn);
            }

            setCellByCol(sheet, TOTAL_ROW, "N", totalMakeQty);

            // 제조공정
            setCellByCol(sheet, 8, "S",makeEtcVo == null ? null : makeEtcVo.getMatProc());
            // 조작조건
            setCellByCol(sheet, 8, "AC",makeEtcVo == null ? null : makeEtcVo.getManipulate());
            // 공정시간
            setCellByCol(sheet, 8, "AG",makeEtcVo == null ? null : makeEtcVo.getProcessTime());

            String prodQty = "";

            if (workOrderInfoVo != null &&
                    workOrderInfoVo.getProdQty() != null) {
                prodQty = workOrderInfoVo.getProdQty() + " kg";
            }
            // 제조량
            setCellByCol(sheet, 8, "AL", prodQty);
            setCellByCol(sheet, 21, "AJ",makeEtcVo == null ? null : makeEtcVo.getSignificant());
            setCellByCol(sheet, 30, "AJ",makeEtcVo == null ? null : makeEtcVo.getNote());
            // 여과포 수량
            setCellByCol(sheet, 38, "AM",makeEtcVo == null ? null : makeEtcVo.getFilterCnt());
            // 여과포 종류
            setCellByCol(sheet, 39, "AM",makeEtcVo == null ? null : makeEtcVo.getFilterType());
            // 여과포 손상
            setCellByCol(sheet, 40, "AM",makeEtcVo == null ? null : makeEtcVo.getFilterDamage());
            // 이물질 확인
            setCellByCol(sheet, 41, "AM",makeEtcVo == null ? null : makeEtcVo.getSubstanceCheck());

            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception e) {

            e.printStackTrace();

            throw new BusinessException("엑셀파일 생성중 에러발생!");
        }
    }

    /**
     * 컬럼 문자 기반 셀 입력
     */
    private void setCellByCol(
            Sheet sheet,
            int rowNum,
            String col,
            Object value
    ) {
        int colIndex = CellReference.convertColStringToIndex(col);
        setCell(sheet, rowNum, colIndex, value);
    }

    private void setCell(Sheet sheet, int rowNum, int colNum, Object value) {
        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);

        Cell cell = row.getCell(colNum);
        if (cell == null) cell = row.createCell(colNum);

        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else {
            cell.setCellValue(String.valueOf(value));
        }
    }

    private void copyRow(Workbook workbook, Sheet sheet, int sourceRowNum, int targetRowNum) {
        Row sourceRow = sheet.getRow(sourceRowNum);
        Row targetRow = sheet.getRow(targetRowNum);

        if (sourceRow == null) return;
        if (targetRow == null) targetRow = sheet.createRow(targetRowNum);

        targetRow.setHeight(sourceRow.getHeight());

        for (int i = sourceRow.getFirstCellNum(); i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell == null) continue;

            Cell targetCell = targetRow.getCell(i);
            if (targetCell == null) targetCell = targetRow.createCell(i);

            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(sourceCell.getCellStyle());
            targetCell.setCellStyle(style);
        }
    }




}
