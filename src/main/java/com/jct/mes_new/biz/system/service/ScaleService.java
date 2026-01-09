package com.jct.mes_new.biz.system.service;

import com.jct.mes_new.biz.system.vo.ScaleVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;

import java.util.List;

public interface ScaleService {
    List<ScaleVo> getScaleList(ScaleVo scaleVo);

    ScaleVo getScaleInfo(String scaleId);

    ScaleVo saveScaleInfo(ScaleVo vo);
}
