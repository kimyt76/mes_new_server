package com.jct.mes_new.biz.base.service.impl;

import com.jct.mes_new.biz.base.mapper.ProdMapper;
import com.jct.mes_new.biz.base.service.ProdService;
import com.jct.mes_new.biz.base.vo.ProdVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProdServiceImpl implements ProdService {

    private final ProdMapper prodMapper;


    public List<ProdVo> getProdPerformanc(ProdVo vo){
        return prodMapper.getProdPerformanc(vo);
    }

    public List<ProdVo> getProdCompany(ProdVo vo){
        return prodMapper.getProdCompany(vo);
    }
}
