package com.jct.mes_new.biz.order.service.impl;

import com.jct.mes_new.biz.order.mapper.ProgressMapper;
import com.jct.mes_new.biz.order.service.ProgressService;
import com.jct.mes_new.biz.order.vo.ProgressVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProgressServiceImpl implements ProgressService {

    private final ProgressMapper progressMapper;

    public List<ProgressVo> getProgressList(ProgressVo progressVo){
        return progressMapper.getProgressList(progressVo);
    }
}
