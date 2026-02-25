package com.jct.mes_new.biz.proc.service;


import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;

import java.util.List;

public interface ProcCoatingService {
    List<ProcCoatingVo> getCoatingList(ProcCoatingVo vo);
}
