package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcMakeMapper;
import com.jct.mes_new.biz.proc.service.ProcMakeService;
import com.jct.mes_new.biz.proc.vo.MakeInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProcMakeServiceImpl implements ProcMakeService {

    private final ProcMakeMapper procMakeMapper;


    public List<ProcMakeVo> getMatList(ProcMakeVo vo){
        return procMakeMapper.getMatList(vo);
    }

    public MakeInfoVo getMakeInfo(ProcMakeVo vo){
        MakeInfoVo info = new MakeInfoVo();

        info.setProcMake(procMakeMapper.getMakeHeadInfo(vo.getWorkProcId()));
        List<ProcWeighBomVo> recipeList = null;

        if ( vo.getMakeId() > 0 ){
            recipeList = procMakeMapper.getRealBomMakeList(vo.getWorkBatchId(), vo.getItemCd());
        }else{
            recipeList = procMakeMapper.getBomMakeList(vo.getWorkBatchId(), vo.getItemCd());
        }
        info.setBomList(recipeList);

        return info;
    }



}
