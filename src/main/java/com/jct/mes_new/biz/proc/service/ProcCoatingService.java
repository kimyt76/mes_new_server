package com.jct.mes_new.biz.proc.service;


import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcProdInfoVo;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;

import java.util.List;

public interface ProcCoatingService {
    List<ProcCoatingVo> getCoatingList(ProcCoatingVo vo);

    ProcProdInfoVo getCoatingInfo(ProcCommonVo vo);

    String startProcCoating(ProcCommonVo vo);
}
