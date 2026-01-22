package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.ProcCommonVo;

import java.util.List;

public interface ProcCommonService {
    List<ProcCommonVo> getWorkerList(String procCd);

    List<ProcCommonVo> getEquipmentList(String storageCd);

}
