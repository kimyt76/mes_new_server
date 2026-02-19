package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.biz.proc.vo.WeighInfoVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcWeighServiceImpl implements ProcWeighService {

    private final ProcWeighMapper procWeighMapper;

    public List<ProcWeighVo> getWeighList(ProcWeighVo vo){
        return procWeighMapper.getWeighList(vo);
    }

    public ProcWeighVo getWeighInfo(String workProcId, String itemCd){
        ProcWeighVo vo = procWeighMapper.getWeighHeadInfo(workProcId);
        List<ProcWeighVo.weigh> recipeList = null;

        if (procWeighMapper.checkWeighCnt(workProcId) > 0 ){
            recipeList = procWeighMapper.getRealBomWeighList(workProcId, itemCd);
        }else{
            recipeList = procWeighMapper.getBomWeighList(workProcId, itemCd);
        }
        vo.setWeighs(recipeList);

        return  vo;
    }
}
