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
        if (procCommonMapper.updateBatchStatus(comVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

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

                int existsCnt = procWeighMapper.countWeighInvItem(item);

                if (existsCnt == 0) {
                    // 신규 등록
                    int insertCnt = procWeighMapper.insertWeighInvItem(item);
                    if (insertCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_CREATED);
                    }
                } else {
                    // 기존 수정
                    int updateCnt = procWeighMapper.updateWeighInvItem(item);
                    if (updateCnt <= 0) {
                        throw new BusinessException(ErrorCode.FAIL_UPDATED);
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
        procWeighMapper.updateWeighRecipe(weigh);

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
            if(cntWeigh <= 0) {
                if (procWeighMapper.insertWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                item.setUserId(userId);
                if (procWeighMapper.updateWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return mst.getWorkProcId();
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
        invMst.setEndYn("Y");
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
        if ( procCommonMapper.updateBatchStatus(comVo) <= 0) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        return vo.getWorkProcId();
    }

    public List<TranLedgerVo> getWeighCloseList(TranLedgerVo vo){
        return procWeighMapper.getWeighCloseList(vo);
    }

    public List<TranLedgerVo> getItemCloseList(TranLedgerVo vo){
        return procWeighMapper.getItemCloseList(vo);
    }

    public byte[] downloadWeighProc(ProcWeighVo vo){
        WorkOrderInfoVo workOrderInfo = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(),  vo.getWorkProcId());

        List<ProcWeighBomVo> weighList = procWeighMapper.getRealBomWeighList(vo.getWorkProcId(), vo.getItemCd());

        try {
            int size = weighList.size();
            String templateName = "/excel/prod_weigh_record_page";
            if(size > 56) {
                templateName += "3.xlsx";
            } else if (size > 25) {
                templateName += "2.xlsx";
            } else {
                templateName += "1.xlsx";
            }

            InputStream excelStream = getClass().getResourceAsStream(templateName);
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);
            Sheet sheet = workbook.getSheet("Sheet1");

            ExcelStyleUtil.getCellRef(sheet, "U1").setCellValue(workOrderInfo.getItemCd()); //품목코드
            ExcelStyleUtil.getCellRef(sheet, "F2").setCellValue(workOrderInfo.getItemName()); //품목명
            ExcelStyleUtil.getCellRef(sheet, "E5").setCellValue(workOrderInfo.getClientName()); //고객사
            if(workOrderInfo.getProdDate() != null) {
                String prodDateFormat = workOrderInfo.getProdDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                ExcelStyleUtil.getCellRef(sheet, "Q5").setCellValue(prodDateFormat); //제조일자
            }
            double prodQty = (workOrderInfo.getProdQty() != null )? workOrderInfo.getProdQty().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "E6").setCellValue(prodQty); //제조량
            ExcelStyleUtil.getCellRef(sheet, "Q6").setCellValue(workOrderInfo.getMakeNo()); //제조번호

            int rowNo = 11;
            List<String> weighMembers = new ArrayList<>();
            List<String> weighConfirmMembers = new ArrayList<>();

            for (ProcWeighBomVo item : weighList) {
                //품목코드
                ExcelStyleUtil.getCellRef(sheet, "B"+rowNo).setCellValue(item.getItemCd());
                //품목명
                ExcelStyleUtil.getCellRef(sheet, "E"+rowNo).setCellValue(item.getItemName());
                //상 구분
                ExcelStyleUtil.getCellRef(sheet, "K"+rowNo).setCellValue(item.getPhase());
                //칭량지시량
                double reqQty = (item.getOrderQty() != null )? item.getOrderQty().doubleValue() : 0;
                ExcelStyleUtil.getCellRef(sheet, "N"+rowNo).setCellValue(reqQty);
                //기준량(함량%)
                double contentRatio = reqQty/prodQty * 100;
                ExcelStyleUtil.getCellRef(sheet, "L"+rowNo).setCellValue(contentRatio);
                //시험번호
                ExcelStyleUtil.getCellRef(sheet, "Q"+rowNo).setCellValue(item.getTestNoJoin());
                //실칭량량
                double weighQty = (item.getWeighQty() != null )? item.getWeighQty().doubleValue() : 0;
                ExcelStyleUtil.getCellRef(sheet, "T"+rowNo).setCellValue(weighQty);
                //작업자
                ExcelStyleUtil.getCellRef(sheet, "W"+rowNo).setCellValue(item.getWeigher());

                if(item.getWeigher() != null) weighMembers.add(item.getWeigher());
                if(item.getConfirmer() != null) weighConfirmMembers.add(item.getConfirmer());

                rowNo++;
            }

            ExcelStyleUtil.getCellRef(sheet, "E7").setCellValue(distinctAndJoining(weighMembers)); //작업자
            ExcelStyleUtil.getCellRef(sheet, "Q7").setCellValue(distinctAndJoining(weighConfirmMembers)); //확인자
            //L36, N36, T36
            if (templateName.equals("/excel/prod_weigh_record_page1.xlsx")) {
                ExcelStyleUtil.getCellRef(sheet, "L36").setCellFormula("SUM(L11:M35)");
                ExcelStyleUtil.getCellRef(sheet, "N36").setCellFormula("SUM(N11:P35)");
                ExcelStyleUtil.getCellRef(sheet, "T36").setCellFormula("SUM(T11:V35)");

            } else if (templateName.equals("/excel/prod_weigh_record_page2.xlsx")) {
                ExcelStyleUtil.getCellRef(sheet, "L67").setCellFormula("SUM(L11:M66)");
                ExcelStyleUtil.getCellRef(sheet, "N67").setCellFormula("SUM(N11:P66)");
                ExcelStyleUtil.getCellRef(sheet, "T67").setCellFormula("SUM(T11:V66)");

            } else {
                ExcelStyleUtil.getCellRef(sheet, "L98").setCellFormula("SUM(L11:M97)");
                ExcelStyleUtil.getCellRef(sheet, "N98").setCellFormula("SUM(N11:P97)");
                ExcelStyleUtil.getCellRef(sheet, "T98").setCellFormula("SUM(T11:V97)");
            }

            return ExcelStyleUtil.toByteArray(workbook);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new BusinessException("엑셀파일 생성중 에러발생!");
        }
    }

    public String distinctAndJoining (List<String> stringList) {
        return stringList.stream().distinct().collect(Collectors.joining(", "));
    }

}
