package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcChargeMapper;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.service.ProcChargeService;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcUseInfoVo;
import com.jct.mes_new.biz.purchase.mapper.TranMapper;
import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.qc.mapper.ItemTestMapper;
import com.jct.mes_new.biz.qc.mapper.QcTestMapper;
import com.jct.mes_new.biz.qc.vo.ItemTestVo;
import com.jct.mes_new.biz.qc.vo.QcTestVo;
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
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcChargeServiceImpl implements ProcChargeService {

    private final ProcChargeMapper procChargeMapper;
    private final ItemTestMapper itemTestMapper;
    private final WorkOrderMapper workOrderMapper;
    private final ProcCommonMapper procCommonMapper;
    private final QcTestMapper qcTestMapper;
    private final TranMapper tranMapper;



    /**
     * 충전작업 시작
     * @param vo
     * @return
     */
    @Transactional(rollbackFor = BusinessException.class)
    public String startProcCharge(ProcChargeVo vo){
        String userId = UserUtil.getUserId();
        vo.setUserId(userId);

        ProcCommonVo procCommonVo = new ProcCommonVo();
        procCommonVo.setWorkBatchId(vo.getWorkBatchId());
        procCommonVo.setBatchStatus(vo.getBatchStatus());
        procCommonVo.setUserId(userId);

        if ( procCommonMapper.updateBatchStatus(procCommonVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
        //충전에 제조 테스트 번호 추가
        //testNo 추출
        String prefix = CodeUtil.createTestNo(LocalDate.now(), vo.getAreaCd(), "M6");
        Integer nextSeq = itemTestMapper.getNextTestNoSeq(prefix);
        String testNo = prefix + String.format("%03d", nextSeq);

        vo.setTestNo(testNo);
        if ( procChargeMapper.startProcCharge(vo) <= 0  ){
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
        itemTestVo.setItemTypeCd("M6");
        itemTestVo.setSeq(1);
        itemTestVo.setItemCd(vo.getItemCd());
        itemTestVo.setItemName(workOrderInfo.getItemName());
        itemTestVo.setLotNo(workOrderInfo.getLotNo());
        itemTestVo.setMakeNo(workOrderInfo.getMakeNo());
        itemTestVo.setQty(workOrderInfo.getOrderQty());
        itemTestVo.setCustomerCd(workOrderInfo.getClientId());
        itemTestVo.setExpiryDate(expiryDate);
        itemTestVo.setShelfLife(shelfLife);
        itemTestVo.setTestState("REG");
        itemTestVo.setPassState("REG");
        itemTestVo.setEndYn("N");
        itemTestVo.setUserId(userId);

        if ( itemTestMapper.insertItemTestNo(itemTestVo) <= 0 ) {
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //품질검사 등록
        QcTestVo qcTestVo = new QcTestVo();
        qcTestVo.setTestNo(testNo);
        qcTestVo.setReqDate(LocalDate.now());
        qcTestVo.setRetestYn("N");
        qcTestVo.setAreaCd(vo.getAreaCd());
        qcTestVo.setStorageCd(vo.getStorageCd());
        qcTestVo.setReqTesterId(userId);
        qcTestVo.setReqQty(BigDecimal.ZERO);

        if(qcTestMapper.insertSingleQcTest(qcTestVo, userId) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        return  "저장되었습니다.";
    }


    @Transactional(rollbackFor = BusinessException.class)
    public String completeCharge(ProcChargeVo vo){
        String userId = UserUtil.getUserId();
        //포장품 제품 입고
        //재고 마스터
        WorkOrderInfoVo workOrder = workOrderMapper.getWorkOrderProcInfo(vo.getProcCd(), vo.getWorkProcId());
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
        invMst.setEndYn("Y");
        invMst.setTranStatus("C");
        invMst.setPoNo(workOrder.getPoNo());
        invMst.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }

        //재고 상세
        TranItemVo tranItemVo = new TranItemVo();
        tranItemVo.setTranId(invMst.getTranId());
        tranItemVo.setItemTypeCd("M6");
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
        //반제품 제조출고
        //재고 마스터
        TranVo invMst2 = new TranVo();
        invMst2.setTranDate(LocalDate.now());
        invMst2.setTranTypeCd("E");
        invMst2.setAreaCd(workOrder.getAreaCd());
        invMst2.setTarStorageCd(workOrder.getStorageCd());
        invMst2.setSrcStorageCd(storageCd);
        invMst2.setEndYn("Y");
        invMst2.setTranStatus("C");
        invMst2.setPoNo(workOrder.getPoNo());
        invMst2.setUserId(userId);

        if (  tranMapper.insertTranMst(invMst2) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_CREATED);
        }
        //재고 상세
        List<ProcUseInfoVo> prodUseList = procCommonMapper.getProdUse(vo.getWorkProcId());
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

        return  "저장되었습니다.";
    }





}
