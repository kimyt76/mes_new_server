package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcPackingVo;

import java.util.List;

public interface ProcPackingService {
    List<ProcChargeVo> getPackingList(ProcPackingVo vo);

    String startProcPacking(ProcPackingVo vo);

    String completePacking(ProcPackingVo vo);
}
