package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcCoatingMapper;
import com.jct.mes_new.biz.proc.service.ProcCoatingService;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import com.jct.mes_new.biz.system.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcCoatingServiceImpl implements ProcCoatingService {

    private final ProcCoatingMapper procCoatingMapper;

    public List<ProcCoatingVo> getCoatingList(ProcCoatingVo vo){
        return procCoatingMapper.getCoatingList(vo);
    }

}
