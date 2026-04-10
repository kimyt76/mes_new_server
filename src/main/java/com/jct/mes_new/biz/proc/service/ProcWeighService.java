package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.biz.proc.vo.WeighInfoVo;
import com.jct.mes_new.biz.proc.vo.WeighInvInfo;

import java.util.List;

public interface ProcWeighService {
    List<ProcWeighVo> getWeighList(ProcWeighVo vo);

    WeighInfoVo getWeighInfo(ProcWeighVo vo);

    Long saveWeighInfo(WeighInfoVo vo);


    ProcWeighVo getWeighHeadInfo(Long workProcId);

    String saveWeighList(WeighInvInfo vo);

    List<ProcWeighVo> getStockTestNoList(ProcWeighVo vo);

    Long completeWeight(ProcWeighVo vo);

    String startProcWeigh(ProcWeighVo vo);
}
