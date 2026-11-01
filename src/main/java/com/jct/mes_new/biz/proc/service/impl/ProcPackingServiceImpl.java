package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.lab.mapper.BomMapper;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcPackingMapper;
import com.jct.mes_new.biz.proc.service.ProcCommonService;
import com.jct.mes_new.biz.proc.service.ProcPackingService;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcPackingVo;
import com.jct.mes_new.biz.proc.vo.ProcUseInfoVo;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.work.mapper.WorkOrderMapper;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import com.jct.mes_new.config.util.CodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcPackingServiceImpl implements ProcPackingService {

    private final ProcPackingMapper procPackingMapper;
    private final ProcCommonMapper procCommonMapper;
    private final ItemTestMapper itemTestMapper;
    private final QcTestMapper qcTestMapper;
    private final WorkOrderMapper workOrderMapper;
    private final BomMapper bomMapper;
    private final TranMapper tranMapper;
    private final ProcCommonService procCommonService;




    @Transactional(rollbackFor = BusinessException.class)
    public String startProcPacking(ProcPackingVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        ProcCommonVo procCommonVo = new ProcCommonVo();
        procCommonVo.setWorkBatchId(vo.getWorkBatchId());
        procCommonVo.setBatchStatus(vo.getBatchStatus());
        procCommonVo.setUserId(userId);

        if ( procCommonMapper.updateBatchStatus(procCommonVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        //포장에 테스트 번호 추가
        //testNo 추출
        String prefix = CodeUtil.createTestNo(LocalDate.now(), vo.getAreaCd(), "M0");
        Integer nextSeq = itemTestMapper.getNextTestNoSeq(prefix);
        String testNo = prefix + String.format("%03d", nextSeq);

        vo.setTestNo(testNo);
        if ( procPackingMapper.startProcPacking(vo) <= 0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        WorkOrderInfoVo workOrderInfo =  workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());

        //시험번호 등록
        ItemTestVo itemTestVo = new ItemTestVo();

        LocalDate shelfLife = LocalDate.now().plusDays(365 * 2);
        LocalDate expiryDate = LocalDate.now().plusDays(365 * 2);

        itemTestVo.setTestNo(testNo);
        itemTestVo.setCreateDate(LocalDate.now());
        itemTestVo.setAreaCd(vo.getAreaCd());
        itemTestVo.setItemTypeCd("M0");
        itemTestVo.setSeq(1);
        itemTestVo.setItemCd(vo.getItemCd());
        itemTestVo.setLotNo(workOrderInfo.getLotNo());
        itemTestVo.setMakeNo(workOrderInfo.getMakeNo());
        itemTestVo.setQty(workOrderInfo.getOrderQty());
        itemTestVo.setCustomerCd(workOrderInfo.getClientId());
        itemTestVo.setExpiryDate(expiryDate);
        itemTestVo.setShelfLife(shelfLife);
        itemTestVo.setTestState("END");
        itemTestVo.setPassState("PASS");
        itemTestVo.setEndYn("N");
        itemTestVo.setUserId(userId);

        if ( itemTestMapper.insertItemTestNo(itemTestVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        List<ProcUseInfoVo> prodList = procCommonMapper.getProdList(vo.getProcCd(), vo.getWorkProcId());
        if ( !prodList.isEmpty() ){
            for ( ProcUseInfoVo item : prodList ){
                item.setUserId(userId);
                item.setWorkProcId(vo.getWorkProcId());
                procCommonMapper.insertProdInfo(item);
            }
        }

        return  "저장되었습니다.";
    }


    @Transactional(rollbackFor = BusinessException.class)
    public String completePacking(ProcPackingVo vo){
        String userId = UserUtil.getUserId();
        Long workProcId = vo.getWorkProcId();

        // 1. 동일 공정 잠금 및 재고 상태 조회
        WorkOrderInfoVo tranStatus = procCommonMapper.getTranStatus(workProcId);

        if (tranStatus == null) {
            throw new BusinessException("작업공정 정보가 없습니다.");
        }

        //완제품 제품 입고
        //재고 마스터
        WorkOrderInfoVo workOrder = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());

        //공정에 수량 업데이트
        ProcUseInfoVo procUseInfoVo = procCommonMapper.getProdUseQty(workProcId);
        workOrder.setUserId(userId);
        workOrder.setProdQty(procUseInfoVo.getProdQty());
        workOrder.setUseQty(procUseInfoVo.getUseQty());

        //수욜 공식이 생기면
        BigDecimal prodYield = BigDecimal.ZERO;
        if (procUseInfoVo.getProdQty() != null && procUseInfoVo.getUseQty() != null
                && procUseInfoVo.getUseQty().compareTo(BigDecimal.ZERO) > 0) {

            prodYield = procUseInfoVo.getProdQty()
                    .divide(procUseInfoVo.getUseQty(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }
        workOrder.setProdYield(prodYield);
        workOrderMapper.updateProdQty(workOrder);

        //배치 업데이트
        ProcCommonVo procCommonVo = new ProcCommonVo();
        procCommonVo.setWorkBatchId(vo.getWorkBatchId());
        procCommonVo.setBatchStatus(vo.getBatchStatus());
        procCommonVo.setWorkProcId(vo.getWorkProcId());
        procCommonVo.setProcStatus(vo.getProcStatus());
        procCommonVo.setUserId(userId);

        if( procCommonMapper.updateBatchStatus(procCommonVo)  <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //proc 업데이트
        if( procCommonMapper.updateProcStatus(procCommonVo)  <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        /**************************** 재고 연결 정보 체크*****************************************************/
        Long inTranId = tranStatus.getInTranId();
        Long outTranId = tranStatus.getOutTranId();

        String tranYn = tranStatus.getTranYn();

        boolean hasIn = inTranId != null;
        boolean hasOut = outTranId != null;
        boolean completed = "Y".equals(tranYn);


        // 4. 비정상 상태 차단
        if (hasIn != hasOut) {
            throw new BusinessException(
                    "입고 및 출고 전표 정보가 일치하지 않습니다."
            );
        }

        if (completed && !hasIn) {
            throw new BusinessException(
                    "재고 처리 완료 상태이나 전표 정보가 없습니다."
            );
        }

        if (!completed && hasIn) {
            throw new BusinessException(
                    "재고 전표는 존재하지만 완료 상태가 아닙니다."
            );
        }

        //공정 완료후 수정건일때 만 타야함
        if (workOrder.getInTranId() != null) {
            procCommonService.updateReworkInfo(workOrder);
            return "수정되었습니다.";
        }
        /**************************** 재고 연결 정보 체크*****************************************************/

        TranVo invMst = new TranVo();

        invMst.setTranDate(LocalDate.now());
        invMst.setTranTypeCd("B");
        invMst.setAreaCd(workOrder.getAreaCd());
        invMst.setTarStorageCd(workOrder.getStorageCd());
        String storageCd = "";
        if ( "A001".equals(workOrder.getAreaCd())){
            storageCd = "WS005";
        }else if ( "A002".equals(workOrder.getAreaCd())){
            storageCd = "WA005";
        }else{
            storageCd = "WS005";
        }
        invMst.setSrcStorageCd(storageCd);
        invMst.setManagerId(userId);
        invMst.setPoNo(workOrder.getPoNo());
        invMst.setEndYn("Y");
        invMst.setTranStatus("C");
        invMst.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //재고 상세
        TranItemVo tranItemVo = new TranItemVo();
        tranItemVo.setTranId(invMst.getTranId());
        tranItemVo.setItemTypeCd("M0");
        tranItemVo.setItemCd(workOrder.getItemCd());
        tranItemVo.setItemName(workOrder.getItemName());
        tranItemVo.setLotNo(workOrder.getLotNo());
        tranItemVo.setTestNo(workOrder.getTestNo());
        tranItemVo.setExpiryDate(workOrder.getExpiryDate());
        tranItemVo.setQty(workOrder.getProdQty());
        tranItemVo.setUserId(userId);

        if ( tranMapper.insertTranItem(tranItemVo) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //완제품 tran_id 업데이트   in_tran_id
        workOrder.setInTranId(invMst.getTranId());
        procCommonMapper.updateInTranId(workOrder);

        //포장품 및 부자재 제조출고
        //재고 마스터
        TranVo invMst2 = new TranVo();
        invMst2.setTranDate(LocalDate.now());
        invMst2.setTranTypeCd("E");
        invMst2.setAreaCd(workOrder.getAreaCd());
        invMst2.setSrcStorageCd(workOrder.getStorageCd());
        invMst2.setTarStorageCd(storageCd);
        invMst2.setEndYn("Y");
        invMst2.setTranStatus("C");
        invMst2.setPoNo(workOrder.getPoNo());
        invMst2.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst2) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //재고 상세
        List<ProcUseInfoVo> prodUseList = procCommonMapper.getProdUseWorkProcIdList(vo.getWorkProcId());
        for( ProcUseInfoVo procUseInfo : prodUseList ){
            //재고 상세
            TranItemVo tranItemVo2 = new TranItemVo();

            tranItemVo2.setTranId(invMst2.getTranId());
            tranItemVo2.setItemTypeCd(procUseInfo.getItemTypeCd());
            tranItemVo2.setItemCd(procUseInfo.getItemCd());
            tranItemVo2.setItemName(procUseInfo.getItemName());
            tranItemVo2.setLotNo(procUseInfo.getLotNo());
            tranItemVo2.setTestNo(procUseInfo.getTestNo());
            tranItemVo2.setExpiryDate(procUseInfo.getExpiryDate());
            tranItemVo2.setQty(procUseInfo.getUseQty());
            tranItemVo2.setInYn("Y");
            tranItemVo2.setQcStatus("PASS");
            tranItemVo2.setUserId(userId);

            if ( tranMapper.insertTranItem(tranItemVo2) <= 0 ){
                throw new BusinessException(ErrorCode.FAIL_CREATED);
            }
        }

        //제조출고 tran_id 업데이트
        workOrder.setOutTranId(invMst2.getTranId());
        procCommonMapper.updateOutTranId(workOrder);

        //작업완료 시간 및 재고이동 완료 업데이트
        workOrder.setTranYn("Y");
        procCommonMapper.updateProcCompleteTranYn(workOrder);

        return  "저장되었습니다.";
    }



}
