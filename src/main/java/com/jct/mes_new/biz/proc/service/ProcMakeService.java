package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;

import java.util.List;

public interface ProcMakeService {

    MakeInfoVo getMakeInfo(ProcMakeVo vo);

    String startProcMake(ProcMakeVo vo);

    ProcMakeVo applyMakeQr(Long weighId);

    String saveMakeInfo(MakeInfoVo vo);

    String insertRowMake(ProcWeighBomVo vo);

    byte[] downloadMatProc(ProcMakeVo vo);

    Long completeMake(ProcMakeVo vo);

    List<WorkOrderInfoVo> getMatProcCondList(ProcSearchVo vo);

    List<MatConditionVo> getConditionList(Long workProcId);
}
