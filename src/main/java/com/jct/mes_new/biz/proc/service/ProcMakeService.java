package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.MakeInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;

import java.util.List;

public interface ProcMakeService {
    List<ProcMakeVo> getMatList(ProcMakeVo vo);

    MakeInfoVo getMakeInfo(ProcMakeVo vo);
}
