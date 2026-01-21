package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProcWeighServiceImpl implements ProcWeighService {

    private final ProcWeighMapper procWeighMapper;

    public List<ProcWeighVo> getWeighList(ProcWeighVo vo){
        return procWeighMapper.getWeighList(vo);
    }

}
