package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;

import java.util.List;

public interface ProcCommonService {

    /**
     * 공정 메인 리스트 조회 (공통)
     * @param vo
     * @return
     */
    List<WorkOrderInfoVo> getProcList(ProcSearchVo vo);


    List<ProcCommonVo> getWorkerList(ProcCommonVo vo);

    List<ProcCommonVo> getEquipmentList(String storageCd);

    String updateProcStatus(ProcCommonVo vo);

    List<ProcCommonVo> getBagWeightList();

    List<ProcTranVo> getProcTranList(ProcTranVo vo);

    Long saveProdInfo(ProcUseRequestVo vo);

    List<ProcUseInfoVo> getProdUseList(Long prodUseId);

    byte[] downloadRecord(ProcCommonVo vo);

    ProcProdInfoVo getProcProdInfo(ProcCommonVo vo);

    String saveWorkRecordInfo( List<WorkRecordVo> recordList);

    WorkRecordVo getWorkRecordInfo(Long workRecordId);

    List<ProcItemVo> getProcItemList(List<Long> ids);


}
