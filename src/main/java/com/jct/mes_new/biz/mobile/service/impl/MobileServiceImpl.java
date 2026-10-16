package com.jct.mes_new.biz.mobile.service.impl;

import com.jct.mes_new.biz.mobile.mapper.MobileMapper;
import com.jct.mes_new.biz.mobile.service.MobileService;
import com.jct.mes_new.biz.monitoring.mapper.MonitoringMapper;
import com.jct.mes_new.biz.monitoring.vo.MatMonitoringVo;
import com.jct.mes_new.biz.proc.mapper.ProcCommonMapper;
import com.jct.mes_new.biz.proc.mapper.ProcMakeMapper;
import com.jct.mes_new.biz.proc.service.impl.ProcMakeServiceImpl;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import com.jct.mes_new.biz.work.vo.WorkerVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.util.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MobileServiceImpl implements MobileService {
    private final MobileMapper mobileMapper;
    private final ProcMakeMapper procMakeMapper;
    private final ProcCommonMapper procCommonMapper;
    private final ProcMakeServiceImpl procMakeServiceImpl;


    public List<MatMonitoringVo> getEquipmentList(String areaCd){
        return mobileMapper.getEquipmentList(areaCd);
    }

    public List<WorkOrderInfoVo> getWorkOrderItems(WorkOrderInfoVo vo){
        return mobileMapper.getWorkOrderItems(vo);
    }

    public List<ProcWeighBomVo> getMatUseList(Long workProcId) {
        return procMakeMapper.getRealBomMakeList(workProcId , "PRC001");
    }
    public List<WorkerVo> getWorkerAllList() {return mobileMapper.getWorkerAllList();}

    @Transactional(rollbackFor = Exception.class)
    public List<ProcWeighBomVo> investmentMatUse(ProcMakeVo vo){
        ProcMakeVo procMake = mobileMapper.getProcMake(vo.getWeighId());
        procMake.setWeighId(vo.getWeighId());
        procMake.setMakeTime(LocalDateTime.now());
        procMake.setMakeYn("Y");
        procMake.setMakeQty(procMake.getWeighQty());
        if(procMake.getItemCd().equals("JRW00215") || procMake.getItemCd().equals("JRMSC00011")) {
            procMake.setWeighQty(vo.getWeighQty());
            procMake.setMakeQty(vo.getWeighQty());
            procMake.setWeighYn("Y");
        }
        procMake.setMaker(vo.getMaker());
        procMake.setUserId(vo.getMaker());

        if ( mobileMapper.updateMakeMobile(procMake) <= 0 ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }
//        MatConditionVo cond = mobileMapper.getMatconditionInfo(vo.getWorkProcId(), procMakeVo.getPhase() );
//
//        if ( cond != null ){
//
//        }else{
//
//        }
//
//        cond.setWorkProcId(vo.getWorkProcId());
//        cond.setPhase(procMakeVo.getPhase());
//        cond.setStartTime(LocalDateTime.now());
//        cond.setEndTime(LocalDateTime.now());
//        cond.setUserId(userId);
//
//        if ( mobileMapper.insertCondition(cond) <= 0 ){
//            throw new BusinessException(ErrorCode.FAIL_UPDATED);
//        }
        List<ProcWeighBomVo> list = procMakeMapper.getRealBomMakeList(vo.getWorkProcId() , "PRC001");

        return list;
    }


    public Long startWorkOrderItem(ProcMakeVo vo) {
        //일단 조회
        ProcMakeVo matVo = mobileMapper.getWorkOrderProc(vo.getWorkProcId());

        matVo.setWorkEquipmentCd(vo.getWorkEquipmentCd());
        matVo.setBatchStatus("21");
        matVo.setProcStatus("21");

        String msg = procMakeServiceImpl.startProcMake(matVo);

        return vo.getWorkProcId();
    }


    @Transactional(rollbackFor = Exception.class)
    public Long finishWorkOrderItem(ProcMakeVo vo) {
        //제조 work_proc 조회   vo.getWorkProcId()  이건 칭량  id임
        ProcMakeVo matVo = mobileMapper.getWorkOrderProc(vo.getWorkProcId());

        vo.setUserId(UserUtil.getUserId());
        vo.setItemCd(matVo.getItemCd());
        //제조id matVo.getWorkProcId()
        //작업자 등록
        int cnt =  mobileMapper.getMakeEtc(matVo.getWorkProcId());

        if (cnt > 0 ) {
            mobileMapper.updateMakeEtc(vo);
        }else{
            mobileMapper.insertMakeEtc(vo);
        }

        //공정 및 재고 등록
        matVo.setProcCd("PRC002");
        matVo.setBatchStatus("22");
        matVo.setProcStatus("22");
        matVo.setProdQty(vo.getProdQty());

        Long tranId = procMakeServiceImpl.completeMake(matVo);

        return tranId;
    }
}
