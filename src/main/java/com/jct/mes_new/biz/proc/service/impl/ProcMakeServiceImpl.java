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
import org.apache.poi.ss.util.CellRangeAddress;
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

    /**
     * 제조 상세 조회
     * @param vo
     * @return
     */
    public MakeInfoVo getMakeInfo(ProcMakeVo vo){
        MakeInfoVo info = new MakeInfoVo();
        String procCd = "PRC001";
        //제조량은 칭량정보를 가져오기 때문에 칭량의 workProcId를 칭량으로 가져와야함
        Long workProcId = procMakeMapper.getWorkProcId(vo.getWorkBatchId(), procCd);
        info.setWorkOrderInfo(workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId()));
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
                                    v.getH() != null && !v.getH().isBlank() ? "H : " + v.getH() : null,
                                    v.getP() != null && !v.getP().isBlank() ? "P : " + v.getP() : null,
                                    v.getD1() != null && !v.getD1().isBlank() ? "D1 : " + v.getD1() : null,
                                    v.getD2() != null && !v.getD2().isBlank() ? "D2 : " + v.getD2() : null,
                                    v.getT() != null && !v.getT().isBlank() ? "T : " + v.getT() : null,
                                    v.getM() != null && !v.getM().isBlank() ? "M : " + v.getM() : null,
                                    v.getP2() != null && !v.getP2().isBlank() ? "P2 : " + v.getP2() : null
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
        MakeEtcVo makeEtcVo = procMakeMapper.getMakeEtcInfo(vo.getWorkProcId());

        if (makeEtcVo == null) {
            makeEtcVo = new MakeEtcVo();
        }
        makeEtcVo.setMatProc(result);
        makeEtcVo.setManipulate(result2);

        // BOM 공정정보가 존재하는 경우에만 설정
        if (bomProcList != null && !bomProcList.isEmpty()) {
            makeEtcVo.setBomVer(bomProcList.get(0).getBomVer());
            makeEtcVo.setNote(bomProcList.get(0).getNote());
            makeEtcVo.setSignificant(bomProcList.get(0).getSignificant());
        }

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

        WorkOrderInfoVo workOrderInfo = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());

        LocalDate shelfLife = LocalDate.now().plusDays(365 * 2);
        LocalDate expiryDate = LocalDate.now().plusDays(365 * 2);

        //시험번호별 내역 등록
        ItemTestVo itemTestVo = new ItemTestVo();
        itemTestVo.setTestNo(testNo);
        itemTestVo.setCreateDate(LocalDate.now());
        itemTestVo.setAreaCd(workOrderInfo.getAreaCd());
        itemTestVo.setItemTypeCd("M3");
        itemTestVo.setSeq(1);
        itemTestVo.setItemCd(workOrderInfo.getItemCd());
        itemTestVo.setItemName(workOrderInfo.getItemName());
        itemTestVo.setMakeNo(workOrderInfo.getMakeNo());
        itemTestVo.setLotNo(workOrderInfo.getLotNo());
        itemTestVo.setCustomerCd(workOrderInfo.getClientId());
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
        qcTestVo.setAreaCd(workOrderInfo.getAreaCd());
        qcTestVo.setStorageCd(workOrderInfo.getStorageCd());
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

        //칭량 재고 완료
        Long tranId = procMakeMapper.getWeighOutTranId(vo.getWorkBatchId());

        procMakeMapper.updateWeighTranEndYn(tranId, userId);

        //재고 마스터
        WorkOrderInfoVo workOrder = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());

        TranVo invMst = new TranVo();
        String toStorage = "";
        if ( "A001".equals(workOrder.getAreaCd()) ){
            toStorage = "WS005";
        }else if ( "A002".equals(workOrder.getAreaCd()) ){
            toStorage = "WA005";
        }else{
            toStorage = "WS005";
        }

        invMst.setTranDate(LocalDate.now());
        invMst.setTranTypeCd("B");
        invMst.setAreaCd(workOrder.getAreaCd());
        invMst.setSrcStorageCd(toStorage);
        invMst.setTarStorageCd(workOrder.getStorageCd());
        invMst.setManagerId(workOrder.getManagerId());
        invMst.setPoNo(workOrder.getPoNo());
        invMst.setEndYn("Y");
        invMst.setTranStatus("C");
        invMst.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        if ( itemTestMapper.updateQty(vo.getTestNo(), vo.getProdQty(), userId) <=0 ){
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
        tranItemVo.setQty(vo.getProdQty());
        tranItemVo.setUserId(userId);

        if ( tranMapper.insertTranItem(tranItemVo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        vo.setProdQty(vo.getProdQty());
        vo.setInTranId(invMst.getTranId() );
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

        //시험번호 제조량 업데이트
        itemTestMapper.updateQty(workOrder.getTestNo(), vo.getProdQty(), userId);

        return vo.getWorkProcId();
    }

    public ProcMakeVo applyMakeQr(Long weighId){
        ProcMakeVo makevo = procMakeMapper.getMakeInfo(weighId);

        if (makevo == null) {
            throw new BusinessException("칭량 정보를 찾을 수 없습니다.");
        }

        if ("Y".equals(makevo.getMakeYn())) {
            throw new BusinessException("이미 투입 완료된 QR입니다.");
        }

        procMakeMapper.updateMakeYn(weighId);
        return makevo;
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String insertRowMake(ProcWeighBomVo vo){
        String userId = UserUtil.getUserId();
        return "저장되었습니다.";
    }

    @Transactional(rollbackFor = BusinessException.class)
    public String saveMakeInfo(MakeInfoVo vo){
        String userId = UserUtil.getUserId();
        WorkOrderInfoVo mst = vo.getWorkOrderInfo();
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
        List<ProcWeighBomVo> weighList = Collections.emptyList();

        if (workOrderInfoVo != null) {
            Long weighWorkProcId = procMakeMapper.getWorkProcId(workOrderInfoVo.getWorkBatchId(), "PRC001");
            if (weighWorkProcId != null) {
                List<ProcWeighBomVo> fetched = procMakeMapper.getRealBomMakeList(weighWorkProcId, vo.getItemCd());
                if (fetched != null) weighList = fetched;
            }
        }

        try (InputStream excelStream = getClass().getResourceAsStream("/excel/mat_proc_template.xlsx")) {
            if (excelStream == null) throw new IllegalStateException("Excel template not found");
            try (Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream)) {
                Sheet sheet = workbook.getSheet("Sheet1");
                if (sheet == null) throw new IllegalStateException("Sheet1 not found");

                final int START_ROW = 8;
                final int LAST_DATA_ROW = 44;
                final int TOTAL_ROW = 45;
                final int defaultRowCount = LAST_DATA_ROW - START_ROW + 1;
                final int extraRowCount = Math.max(0, weighList.size() - defaultRowCount);

                // Populate header fields without changing the template's formatting.
                setCellByCol(sheet, 5, "D", workOrderInfoVo == null ? null : workOrderInfoVo.getClientName());
                setCellByCol(sheet, 5, "M", workOrderInfoVo == null ? null : workOrderInfoVo.getMakeNo());
                setCellByCol(sheet, 5, "V", workOrderInfoVo == null ? null : workOrderInfoVo.getWorkEquipmentCd());
                setCellByCol(sheet, 5, "AD", makeEtcVo == null ? null : makeEtcVo.getMaker());
                setCellByCol(sheet, 5, "AL", workOrderInfoVo == null ? null : workOrderInfoVo.getProdDate());
                setCellByCol(sheet, 0, "AK", vo.getItemCd());
                setCellByCol(sheet, 2, "AK", makeEtcVo == null ? null : makeEtcVo.getBomVer());
                setCellByCol(sheet, 2, "I", vo.getItemName());

                if (extraRowCount > 0) {
                    // shiftRows also moves merged ranges below the material table.
                    sheet.shiftRows(TOTAL_ROW, sheet.getLastRowNum(), extraRowCount, true, false);
                    for (int i = 0; i < extraRowCount; i++) {
                        copyRow(workbook, sheet, LAST_DATA_ROW, LAST_DATA_ROW + 1 + i);
                    }
                    // Extend vertical merged ranges crossing the material table's old bottom.
                    // Horizontal/vertical regions that already moved are not modified.
                    extendCrossingMergedRegions(sheet, LAST_DATA_ROW, extraRowCount);
                }

                BigDecimal totalMakeQty = BigDecimal.ZERO;
                for (int i = 0; i < weighList.size(); i++) {
                    ProcWeighBomVo weigh = weighList.get(i);
                    if (weigh == null) continue;
                    int rowNum = START_ROW + i;
                    setCellByCol(sheet, rowNum, "A", String.format("%03d", i + 1));
                    setCellByCol(sheet, rowNum, "C", weigh.getItemCd());
                    setCellByCol(sheet, rowNum, "G", weigh.getItemName());
                    setCellByCol(sheet, rowNum, "L", weigh.getPhase());
                    setCellByCol(sheet, rowNum, "N", weigh.getOrderQty());
                    setCellByCol(sheet, rowNum, "Q", weigh.getMakeQty());
                    setCellByCol(sheet, rowNum, "R", weigh.getMaker());
                    if (weigh.getMakeQty() != null) totalMakeQty = totalMakeQty.add(weigh.getMakeQty());
                }
                setCellByCol(sheet, TOTAL_ROW + extraRowCount, "N", totalMakeQty);

                // Write wrapped text into the top-left cell of each merged range.
                // For multi-row merged ranges, heights are increased only when needed.
                setMergedCellText(sheet, 8, "S", makeEtcVo == null ? null : makeEtcVo.getMatProc());
                setMergedCellText(sheet, 8, "AC", makeEtcVo == null ? null : makeEtcVo.getManipulate());
                setMergedCellText(sheet, 8, "AG", makeEtcVo == null ? null : makeEtcVo.getProcessTime());
                setMergedCellText(sheet, 21, "AJ", makeEtcVo == null ? null : makeEtcVo.getSignificant());
                setMergedCellText(sheet, 30, "AJ", makeEtcVo == null ? null : makeEtcVo.getNote());

                String prodQty = workOrderInfoVo != null && workOrderInfoVo.getProdQty() != null
                        ? workOrderInfoVo.getProdQty() + " kg" : "";
                setCellByCol(sheet, 8, "AL", prodQty);
                setCellByCol(sheet, 38, "AM", makeEtcVo == null ? null : makeEtcVo.getFilterCnt());
                setCellByCol(sheet, 39, "AM", makeEtcVo == null ? null : makeEtcVo.getFilterType());
                setCellByCol(sheet, 40, "AM", makeEtcVo == null ? null : makeEtcVo.getFilterDamage());
                setCellByCol(sheet, 41, "AM", makeEtcVo == null ? null : makeEtcVo.getSubstanceCheck());

                return ExcelStyleUtil.toByteArray(workbook);
            }
        } catch (Exception e) {
            // Replace with your project's logger if available.
            e.printStackTrace();
            throw new BusinessException("엑셀파일 생성중 에러발생!");
        }
    }

    private void setCellByCol(Sheet sheet, int rowNum, String col, Object value) {
        setCell(sheet, rowNum, CellReference.convertColStringToIndex(col), value);
    }

    private void setCell(Sheet sheet, int rowNum, int colNum, Object value) {
        Row row = sheet.getRow(rowNum);
        if (row == null) row = sheet.createRow(rowNum);
        Cell cell = row.getCell(colNum);
        if (cell == null) cell = row.createCell(colNum);
        if (value == null) cell.setBlank();
        else if (value instanceof Number) cell.setCellValue(((Number) value).doubleValue());
        else cell.setCellValue(String.valueOf(value));
    }

    private void copyRow(Workbook workbook, Sheet sheet, int sourceRowNum, int targetRowNum) {
        Row sourceRow = sheet.getRow(sourceRowNum);
        if (sourceRow == null) return;
        Row targetRow = sheet.getRow(targetRowNum);
        if (targetRow == null) targetRow = sheet.createRow(targetRowNum);
        targetRow.setHeight(sourceRow.getHeight());
        for (int i = Math.max(0, sourceRow.getFirstCellNum()); i < sourceRow.getLastCellNum(); i++) {
            Cell sourceCell = sourceRow.getCell(i);
            if (sourceCell == null) continue;
            Cell targetCell = targetRow.getCell(i);
            if (targetCell == null) targetCell = targetRow.createCell(i);
            // Reuse immutable template styles instead of creating one style per cell.
            targetCell.setCellStyle(sourceCell.getCellStyle());
        }
    }

    private void extendCrossingMergedRegions(Sheet sheet, int oldLastDataRow, int extraRows) {
        if (extraRows <= 0) return;
        List<CellRangeAddress> ranges = new ArrayList<>(sheet.getMergedRegions());
        for (CellRangeAddress region : ranges) {
            if (region.getFirstRow() <= oldLastDataRow && region.getLastRow() >= oldLastDataRow
                    && region.getFirstRow() < region.getLastRow()) {
                int index = findExactMergedRegionIndex(sheet, region);
                if (index < 0) continue;
                sheet.removeMergedRegion(index);
                sheet.addMergedRegion(new CellRangeAddress(region.getFirstRow(), region.getLastRow() + extraRows,
                        region.getFirstColumn(), region.getLastColumn()));
            }
        }
    }

    private int findExactMergedRegionIndex(Sheet sheet, CellRangeAddress target) {
        for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
            CellRangeAddress region = sheet.getMergedRegion(i);
            if (region.getFirstRow() == target.getFirstRow() && region.getLastRow() == target.getLastRow()
                    && region.getFirstColumn() == target.getFirstColumn() && region.getLastColumn() == target.getLastColumn()) {
                return i;
            }
        }
        return -1;
    }

    private void setMergedCellText(Sheet sheet, int rowNum, String col, Object value) {
        int colIndex = CellReference.convertColStringToIndex(col);
        CellRangeAddress merged = null;
        for (CellRangeAddress region : sheet.getMergedRegions()) {
            if (region.isInRange(rowNum, colIndex)) {
                merged = region;
                break;
            }
        }
        int anchorRow = merged == null ? rowNum : merged.getFirstRow();
        int anchorCol = merged == null ? colIndex : merged.getFirstColumn();
        Row row = sheet.getRow(anchorRow);
        if (row == null) row = sheet.createRow(anchorRow);
        Cell cell = row.getCell(anchorCol);
        if (cell == null) cell = row.createCell(anchorCol);

        String text = value == null ? "" : String.valueOf(value).replace("\r\n", "\n").replace('\r', '\n');
        cell.setCellValue(text);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        style.cloneStyleFrom(cell.getCellStyle());
        style.setWrapText(true);
        style.setVerticalAlignment(VerticalAlignment.TOP);
        cell.setCellStyle(style);

        if (text.isEmpty()) return;
        int firstCol = merged == null ? anchorCol : merged.getFirstColumn();
        int lastCol = merged == null ? anchorCol : merged.getLastColumn();
        double widthChars = 0;
        for (int c = firstCol; c <= lastCol; c++) {
            if (!sheet.isColumnHidden(c)) widthChars += sheet.getColumnWidth(c) / 256.0;
        }
        widthChars = Math.max(3, widthChars - 3);
        int lines = estimateVisualLines(text, widthChars);

        Font font = sheet.getWorkbook().getFontAt(style.getFontIndex());
        double linePoints = Math.max(11, font.getFontHeightInPoints() * 1.35);
        double requiredPoints = lines * linePoints + 5;
        int firstRow = merged == null ? anchorRow : merged.getFirstRow();
        int lastRow = merged == null ? anchorRow : merged.getLastRow();
        double currentPoints = 0;
        for (int r = firstRow; r <= lastRow; r++) {
            Row current = sheet.getRow(r);
            currentPoints += current == null ? sheet.getDefaultRowHeightInPoints() : current.getHeightInPoints();
        }
        if (requiredPoints > currentPoints) {
            // Distribute additional height across merged rows (Excel max 409.5pt per row).
            double remaining = requiredPoints - currentPoints;
            for (int r = lastRow; r >= firstRow && remaining > 0.01; r--) {
                Row target = sheet.getRow(r);
                if (target == null) target = sheet.createRow(r);
                double available = Math.max(0, 409.5 - target.getHeightInPoints());
                double add = Math.min(available, remaining);
                target.setHeightInPoints((float) (target.getHeightInPoints() + add));
                remaining -= add;
            }
        }
    }


    private int estimateVisualLines(String text, double widthChars) {

        if (text == null || text.isEmpty()) {
            return 1;
        }

        if (widthChars <= 0) {
            return 1;
        }

        int lines = 0;

        String normalized = text
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        for (String paragraph : normalized.split("\n", -1)) {

            double units = 0;
            int paragraphLines = 1;

            for (int offset = 0; offset < paragraph.length();) {

                int cp = paragraph.codePointAt(offset);
                offset += Character.charCount(cp);

                Character.UnicodeScript script =
                        Character.UnicodeScript.of(cp);

                double charUnits =
                        (script == Character.UnicodeScript.HANGUL
                                || script == Character.UnicodeScript.HAN
                                || script == Character.UnicodeScript.HIRAGANA
                                || script == Character.UnicodeScript.KATAKANA)
                                ? 1.8 : 1.0;

                if (units > 0 && units + charUnits > widthChars) {
                    paragraphLines++;
                    units = 0;
                }

                units += charUnits;
            }

            lines += paragraphLines;
        }

        return Math.max(1, lines);
    }




    public List<WorkOrderInfoVo> getMatProcCondList(ProcSearchVo vo){
        return procMakeMapper.getMatProcCondList(vo);
    }

    public List<MatConditionVo> getConditionList(Long workProcId){
        int cnt =  procMakeMapper.conditionCnt(workProcId);

        List<MatConditionVo> conditionList = null;

        if (cnt <= 0 ) {
            WorkOrderInfoVo worOrder = workOrderMapper.getWorkOrderProcInfo("PRC002", workProcId);
            String itemCd = worOrder.getItemCd();
            return conditionList = procMakeMapper.getRecipeCondition(itemCd);
        }else{
           return  conditionList = procMakeMapper.getMatCondition(workProcId);
        }
    }

}
