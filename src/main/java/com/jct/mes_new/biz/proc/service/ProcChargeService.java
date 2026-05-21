package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;

import java.util.List;

public interface ProcChargeService {
    List<ProcChargeVo> getChargeList(ProcChargeVo vo);

    String startProcCharge(ProcChargeVo vo);

    String completeCharge(ProcChargeVo vo);
}
