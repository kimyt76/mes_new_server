package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcTranVo;

import java.util.List;

public interface ProcCommonService {
    List<ProcCommonVo> getWorkerList(ProcCommonVo vo);

    List<ProcCommonVo> getEquipmentList(String storageCd);

    String updateProcStatus(ProcCommonVo vo);

    List<ProcCommonVo> getBagWeightList();

    List<ProcTranVo> getProcTranList(SearchCommonVo vo);
}
