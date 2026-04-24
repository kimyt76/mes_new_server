package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.*;

import java.util.List;

public interface ProcCommonService {
    List<ProcCommonVo> getWorkerList(ProcCommonVo vo);

    List<ProcCommonVo> getEquipmentList(String storageCd);

    String updateProcStatus(ProcCommonVo vo);

    List<ProcCommonVo> getBagWeightList();

    List<ProcTranVo> getProcTranList(SearchCommonVo vo);

    Long saveProdInfo(ProcUseRequestVo vo);

    List<ProcUseInfoVo> getProdUseList(Long prodInfoId);

    byte[] downloadRecord(ProcCommonVo vo);

    ProcProdInfoVo getProcProdInfo(ProcCommonVo vo);

    String saveWorkRecordInfo(WorkRecordVo vo);
}
