package com.jct.mes_new.biz.system.service.impl;

import com.jct.mes_new.biz.system.mapper.ScaleMapper;
import com.jct.mes_new.biz.system.service.ScaleService;
import com.jct.mes_new.biz.system.vo.ScaleVo;
import com.jct.mes_new.biz.work.vo.WorkOrderVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ScaleServiceImpl implements ScaleService {

    private final ScaleMapper scaleMapper;

    public List<ScaleVo> getScaleList(ScaleVo scaleVo){
        return  scaleMapper.getScaleList(scaleVo);
    }

    public ScaleVo getScaleInfo(String scaleId) {
        return  scaleMapper.getScaleInfo(scaleId);
    }

    public ScaleVo saveScaleInfo(ScaleVo vo){
        return scaleMapper.saveScaleInfo(vo);
    }
}
