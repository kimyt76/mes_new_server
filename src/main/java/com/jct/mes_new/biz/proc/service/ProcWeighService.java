package com.jct.mes_new.biz.proc.service;

import com.jct.mes_new.biz.proc.vo.ProcWeighVo;

import java.util.List;

public interface ProcWeighService {
    List<ProcWeighVo> getWeighList(ProcWeighVo vo);
}
