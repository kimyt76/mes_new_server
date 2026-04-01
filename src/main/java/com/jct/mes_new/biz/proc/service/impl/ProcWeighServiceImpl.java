package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
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

    /**
     * 칭량조회
     * @param vo
     * @return
     */
    public List<ProcWeighVo> getWeighList(ProcWeighVo vo){
        return procWeighMapper.getWeighList(vo);
    }

    /**
     * 칭량 상세 조회
     * @param vo
     * @return
     */
    public WeighInfoVo getWeighInfo(ProcWeighVo vo){
        WeighInfoVo info = new WeighInfoVo();

        info.setProcWeigh(procWeighMapper.getWeighHeadInfo(vo.getWorkProcId()));
        List<ProcWeighBomVo> recipeList = null;

        if (procWeighMapper.checkWeighCnt(vo.getWorkProcId()) > 0 ){
            recipeList = procWeighMapper.getRealBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }else{
            recipeList = procWeighMapper.getBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }
        info.setWeightBomList(recipeList);

        return  info;
    }
}
