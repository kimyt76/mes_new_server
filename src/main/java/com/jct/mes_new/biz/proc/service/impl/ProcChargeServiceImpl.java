package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.mapper.ProcChargeMapper;
import com.jct.mes_new.biz.proc.service.ProcChargeService;
import com.jct.mes_new.biz.proc.vo.ProcChargeVo;
import com.jct.mes_new.biz.proc.vo.ProcCoatingVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcChargeServiceImpl implements ProcChargeService {

    private final ProcChargeMapper procChargeMapper;

    public List<ProcChargeVo> getChargeList(ProcChargeVo vo){
        return procChargeMapper.getChargeList(vo);
    }

}
