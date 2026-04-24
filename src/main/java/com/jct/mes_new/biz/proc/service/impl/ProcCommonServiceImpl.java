package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.base.mapper.ItemMapper;
import com.jct.mes_new.biz.base.vo.ItemVo;
import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.ExcelStyleUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jfree.util.Log;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcCommonServiceImpl implements ProcCommonService {

    private final ProcCommonMapper procCommonMapper;
    private final ItemMapper itemMapper;
    private final WorkOrderMapper workOrderMapper;


    public List<ProcCommonVo> getWorkerList(ProcCommonVo vo){
        return procCommonMapper.getWorkerList(vo.getAreaCd(), vo.getProcCd());
    }
    public List<ProcCommonVo> getBagWeightList(){
        return procCommonMapper.getBagWeightList();
    }

    public List<ProcCommonVo> getEquipmentList(String storageCd){
        return procCommonMapper.getEquipmentList(storageCd);
    }

    /**
     * 작업지시 상태 업데이트
     * @param vo
     * @return
     */
    public String updateProcStatus(ProcCommonVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if (procCommonMapper.updateProcStatus(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        if (procCommonMapper.updateBatchStatus(vo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        return "수정되었습니다.";
    }

    /**
     * 제조출고 조회
     * @param vo
     * @return
     */
    public List<ProcTranVo> getProcTranList(SearchCommonVo vo){
        return procCommonMapper.getProcTranList(vo);
    }

    /**
     * 공정 별 투입량 저장
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long saveProdInfo(ProcUseRequestVo vo){
        String userId = UserUtil.getUserId();

        ProcUseInfoVo mst = vo.getProdInfo();
        List<ProcUseInfoVo> prodUseList = vo.getProdUseList();
        mst.setUserId(userId);

        //투입자재 정보 등록
        Long prodInfoId = procCommonMapper.insertProdInfo(mst);

        if ( prodInfoId == null || prodInfoId <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        log.info("========================prodInfoId============= : " + prodInfoId);
        log.info("========================mst.getProdInfoId()============= : " + mst.getProdInfoId());
        //투입량 저장
        for (ProcUseInfoVo prodUseInfo : prodUseList){
            prodUseInfo.setProdInfoId(mst.getProdInfoId());
            prodUseInfo.setItemCd(mst.getItemCd());
            prodUseInfo.setUserId(userId);

            if ( procCommonMapper.insertProdUseInfo(prodUseInfo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }
        //work_order_proc 작업지시에 투입량 정보 업데이트
        if ( procCommonMapper.updateProdByWorkOrderProc(mst) <= 0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        return prodInfoId;
    }

    /**
     * 작업수행정보 등록
     * @param vo
     * @return
     */
    public String saveWorkRecordInfo(WorkRecordVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        if ( vo.getWorkRecordId() == null || vo.getWorkRecordId() <= 0 ){
            if ( procCommonMapper.insertWorkRecordInfo(vo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }else{
            if ( procCommonMapper.updateWorkRecordInfo(vo) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_UPDATED);
            }
        }
        return "저장되었습니다.";
    }

    /**
     *  사용량 입력 조회
     * @param prodInfoId
     * @return
     */
    public List<ProcUseInfoVo> getProdUseList(Long prodInfoId){
        return procCommonMapper.getProdUseList(prodInfoId);
    }

    public ProcProdInfoVo getProcProdInfo(ProcCommonVo vo){
        ProcProdInfoVo info = new ProcProdInfoVo();

        info.setItemInfo(itemMapper.getItemInfo(vo.getItemCd()));
        info.setProdList(procCommonMapper.getProdList(vo.getProcCd(), vo.getWorkProcId()));
        info.setWorkOrderProcInfo(workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId()) );
        info.setWorkRecordList(procCommonMapper.getWorkRecordList(vo.getWorkProcId()) );

        return info;
    }

    /**
     *  공정별 기록서 다운로드 (코팅, 충전, 포장)
     * @param vo
     * @return
     */
    public byte[] downloadRecord(ProcCommonVo vo){
        ProcCommonVo comVo = new ProcCommonVo();
        comVo.setWorkProcId(vo.getWorkProcId());
        comVo.setProcCd(vo.getProcCd());
        comVo.setItemCd(vo.getItemCd());
        ProcProdInfoVo prodInfo = this.getProcProdInfo(comVo);

        WorkOrderInfoVo workInfo = prodInfo.getWorkOrderProcInfo();
        ItemVo itemInfo = prodInfo.getItemInfo();
        List<ProcUseInfoVo> prodList = prodInfo.getProdList();
        List<WorkRecordVo> workRecordList = prodInfo.getWorkRecordList();
        Boolean isCoating = "PRC003".equals(vo.getProcCd());

        try {
            InputStream excelStream = getClass().getResourceAsStream(getTemplatePathByProcessCd(vo.getProcCd()));
            Workbook workbook = ExcelStyleUtil.createWorkbook(excelStream);
            Sheet sheet = workbook.getSheet("Sheet1");

            /** 타이틀 part **/
            ExcelStyleUtil.getCellRef(sheet, "H3").setCellValue(itemInfo.getItemName()); //품목명
            ExcelStyleUtil.getCellRef(sheet, "AU1").setCellValue(itemInfo.getItemCd());  //품목코드

            /** 작업정보 그리드 part **/
            //고객사 : F6
            ExcelStyleUtil.getCellRef(sheet, "F6").setCellValue(workInfo.getClientName());
            //제품타입 : S6
            ExcelStyleUtil.getCellRef(sheet, "S6").setCellValue(itemInfo.getProdType());
            //작업일자 : AF6
            String orderDateFormat = workInfo.getProdDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            ExcelStyleUtil.getCellRef(sheet, "AF6").setCellValue(orderDateFormat);
            //작업지시량 : AS6
            double orderQty = (workInfo.getOrderQty() != null )? workInfo.getOrderQty().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "AS6").setCellValue(orderQty);
            //제조번호 : F7
            ExcelStyleUtil.getCellRef(sheet, "F7").setCellValue(workInfo.getMakeNo());
            //제조량 : S7
            double producedQty = (workInfo.getProdQty() != null )? workInfo.getProdQty().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "S7").setCellValue(producedQty);
            // 작업처 : F8
            ExcelStyleUtil.getCellRef(sheet, "F8").setCellValue(workInfo.getStorageName());
            // 작업공정도 : F9
            ExcelStyleUtil.getCellRef(sheet, "F9").setCellValue(itemInfo.getWorkFlow());
            // 특이사항 : 코팅일경우 AF9, 충전 포장일 경우 AP7
            String noteCell = (isCoating)?"AF9" : "AP7";
            ExcelStyleUtil.getCellRef(sheet, noteCell).setCellValue(workInfo.getEtc());

            //코팅공정 공유 표시값.
            if (isCoating){
                //폭너비(mm) 및 겔 Sheet 적층 수 BB7, BC7, BB8
                double sheetLength = (itemInfo.getSheetLength() != null)? itemInfo.getSheetLength().doubleValue() : 0;
                double sheetWidth = (itemInfo.getSheetWidth() != null)? itemInfo.getSheetWidth().doubleValue() : 0;

                String sheetStacking = Optional.ofNullable(itemInfo.getSheetStacking()) .orElse(0) + " Sheet 적층 수";
                ExcelStyleUtil.getCellRef(sheet, "AF7").setCellValue(itemInfo.getSheetSpec());
                ExcelStyleUtil.getCellRef(sheet, "AF8").setCellValue(sheetStacking);
                // 기준무게 (필름제외)
                ExcelStyleUtil.getCellRef(sheet, "AS7").setCellValue(itemInfo.getStdWeight());
                String stdSize = "("+ itemInfo.getStdSize() + ")";
                ExcelStyleUtil.getCellRef(sheet, "AS8").setCellValue(stdSize);
                // 목형 No.
                ExcelStyleUtil.getCellRef(sheet, "S8").setCellValue(itemInfo.getWoodenPattern());
            }//충전, 포장 공정 고유 표시값.
            else {
                //Lot 표기 1열 AD7, 2열 AD8
                String[] lotNoArray = workInfo.getLotNo().split("!!");
                if(lotNoArray.length == 2) { //로트번호가 두줄일 경우
                    ExcelStyleUtil.getCellRef(sheet, "AD7").setCellValue(lotNoArray[0]);
                    ExcelStyleUtil.getCellRef(sheet, "AD8").setCellValue(lotNoArray[1]);
                } else { //로트번호가 한줄일 경우
                    ExcelStyleUtil.getCellRef(sheet, "AD7").setCellValue(workInfo.getLotNo());
                    ExcelStyleUtil.getCellRef(sheet, "AD8").setCellValue("");
                }
                // 표시용량
                ExcelStyleUtil.getCellRef(sheet, "S8").setCellValue(itemInfo.getDisplayCapacity());
            }

            /** 시험번호 그리드 **/
            //시작 row 번호 (범위는  13 ~ 22)
            int rowNo = 13;
            double totalQtySum = 0;
            for ( ProcUseInfoVo matUseEtcView : prodList ) {
                //시험번호 C13
                ExcelStyleUtil.getCellRef(sheet, "C"+rowNo).setCellValue(matUseEtcView.getTestNos());
                //재료명 H13
                ExcelStyleUtil.getCellRef(sheet, "H"+rowNo).setCellValue(matUseEtcView.getMatName());
                //규격 N13
                ExcelStyleUtil.getCellRef(sheet, "N"+rowNo).setCellValue(matUseEtcView.getSpecInfo());
                //성상및특성 T13
                ExcelStyleUtil.getCellRef(sheet, "T"+rowNo).setCellValue(matUseEtcView.getExAppearance());

                //충전, 포장일 경우에만
                if(!isCoating){
                    //단위포장규격
                    ExcelStyleUtil.getCellRef(sheet, "AA"+rowNo).setCellValue(matUseEtcView.getPackingSpecValue());
                    ExcelStyleUtil.getCellRef(sheet, "AD"+rowNo).setCellValue(matUseEtcView.getPackingSpecUnit());
                }

                //예상소요량 AI13
                double reqQty = (matUseEtcView.getReqQty() != null)? matUseEtcView.getReqQty().doubleValue() : 0;
                ExcelStyleUtil.getCellRef(sheet, "AI"+rowNo).setCellValue(reqQty);
                //단위 AL13 AQ13  AU13  AY13
                String unit = matUseEtcView.getUnit();
                ExcelStyleUtil.getCellRef(sheet, "AL"+rowNo).setCellValue(unit);
                ExcelStyleUtil.getCellRef(sheet, "AQ"+rowNo).setCellValue(unit);
                ExcelStyleUtil.getCellRef(sheet, "AU"+rowNo).setCellValue(unit);
                ExcelStyleUtil.getCellRef(sheet, "AY"+rowNo).setCellValue(unit);
                //총사용량 AN13 2021-12-21 사용량 -> 총사용량으로 변경
                totalQtySum = (matUseEtcView.getUseQty() != null)? matUseEtcView.getUseQty().doubleValue() : 0;
                ExcelStyleUtil.getCellRef(sheet, "AN"+rowNo).setCellValue(totalQtySum);
                //원불량 AS13
                double badMatQtySum = (matUseEtcView.getBadQty() != null)? matUseEtcView.getBadQty().doubleValue() : 0;
                ExcelStyleUtil.getCellRef(sheet, "AS"+rowNo).setCellValue(badMatQtySum);
                //작업불량  AW13
                double badWorkQtySum = (matUseEtcView.getWorkBadQty() != null)? matUseEtcView.getWorkBadQty().doubleValue() : 0;
                ExcelStyleUtil.getCellRef(sheet, "AW"+rowNo).setCellValue(badWorkQtySum);
                rowNo++;
            }

            /** 작업시간 그리드 **/
            // 3건 이하는 그리드에 표시, 그 이상일 경우는 공백으로 표시.
            int recordSize = workRecordList.size();
            if(recordSize > 0) {
                int timeRowNo = 26; //26 ~ 28
                for ( WorkRecordVo prodRecord : workRecordList ) {
                    //일자  A26  mm월dd형식
                    String recordDateFormat = prodRecord.getWorkDate().format(DateTimeFormatter.ofPattern("MM월dd일"));
                    ExcelStyleUtil.getCellRef(sheet, "A" + timeRowNo).setCellValue(recordDateFormat);
                    //작업시작 ~ 작업종료  00:00형식 시작시간 : D26  종료시간: J26
                    ExcelStyleUtil.getCellRef(sheet, "D" + timeRowNo).setCellValue(prodRecord.getWorkStartTime().toString());
                    ExcelStyleUtil.getCellRef(sheet, "J" + timeRowNo).setCellValue(prodRecord.getWorkEndTime().toString());
                    //작업인원 O26
                    ExcelStyleUtil.getCellRef(sheet, "O" + timeRowNo).setCellValue(prodRecord.getWorkerCnt());
                    //반제품사용량(벌크제품, 포장품 사용량) R26
                    double inputQty = (prodRecord.getUseQty() != null)? prodRecord.getUseQty().doubleValue() : 0;
                    ExcelStyleUtil.getCellRef(sheet, "R" + timeRowNo).setCellValue(inputQty);
                    //코팅수량(충전, 포장 수량) V26
                    double prodQty = (prodRecord.getProdQty() != null)? prodRecord.getProdQty().doubleValue() : 0;
                    ExcelStyleUtil.getCellRef(sheet, "V" + timeRowNo).setCellValue(prodQty);

                    if(timeRowNo == 28) break;
                    timeRowNo++;
                }
            }

            /** 수율 그리드 **/
            //수율( + 계산식 + ) Z24
            ExcelStyleUtil.getCellRef(sheet, "Z24").setCellValue(itemInfo.getDisplayYield());
            // lot수율    기준 00% 이상  AE25
            double stdYield = (itemInfo.getStdYield() != null)? itemInfo.getStdYield().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "AE25").setCellValue(stdYield);
            // 총사용량  AE26
            double matUseQty = (workInfo.getUseQty() != null)? workInfo.getUseQty().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "AE26").setCellValue(matUseQty);
            // 생산수량  AE27
            double prodQty = (workInfo.getProdQty() != null)? workInfo.getProdQty().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "AE27").setCellValue(prodQty);
            // 생산수율  AE28
            double yieldRate = (workInfo.getProdYield() != null)? workInfo.getProdYield().doubleValue() : 0;
            ExcelStyleUtil.getCellRef(sheet, "AE28").setCellValue(yieldRate);

            /** byte[] 변환 */
            return ExcelStyleUtil.toByteArray(workbook);

        }catch(Exception e){
            e.printStackTrace();
            throw new BusinessException("엑셀파일 생성중 에러발생!");
        }
    }

    public String getTemplatePathByProcessCd (String processCd) {
        String templatePath = "";
        //각 공정에 해당하는 양식 설정
        switch (processCd) {
            case "PRC003":
                templatePath = "/excel/coating_template.xlsx"; break;
            case "PRC004":
                templatePath = "/excel/charging_template.xlsx"; break;
            case "PRC005":
                templatePath = "/excel/packing_template.xlsx"; break;
            default:
                break;
        }
        return templatePath;
    }











}
