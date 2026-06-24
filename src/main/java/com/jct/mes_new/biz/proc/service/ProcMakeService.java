package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.MakeInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;

import java.util.List;

public interface ProcMakeService {

    MakeInfoVo getMakeInfo(ProcMakeVo vo);

    String startProcMake(ProcMakeVo vo);

    Long getWeighQty(Long weighId);

    String saveMakeInfo(MakeInfoVo vo);

    String insertRowMake(ProcWeighBomVo vo);

    byte[] downloadMatProc(ProcMakeVo vo);

    Long completeMake(ProcMakeVo vo);
}
