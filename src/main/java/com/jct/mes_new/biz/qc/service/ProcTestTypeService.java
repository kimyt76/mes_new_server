package com.jct.mes_new.biz.qc.service;

import com.jct.mes_new.biz.qc.vo.ProcTestTypeMethodVo;
import com.jct.mes_new.biz.qc.vo.ProcTestTypeVo;

import java.util.List;

public interface ProcTestTypeService {
    List<ProcTestTypeVo> getProcTestTypeList();

    List<ProcTestTypeMethodVo> getProcTestTypeMethod(String testType);

    String saveProcTestTypeMethod(ProcTestTypeMethodVo vo);

    ProcTestTypeMethodVo getProcTestTypeMethodInfo(String procTestTypeMethodId);
}
