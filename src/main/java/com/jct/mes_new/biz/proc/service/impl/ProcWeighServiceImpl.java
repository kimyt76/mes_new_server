package com.jct.mes_new.biz.proc.service.impl;

import com.jct.mes_new.biz.proc.mapper.ProcWeighMapper;
import com.jct.mes_new.biz.proc.service.ProcWeighService;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.biz.proc.vo.WeighInfoVo;
import com.jct.mes_new.config.common.UserUtil;
import com.jct.mes_new.config.common.exception.BusinessException;
import com.jct.mes_new.config.common.exception.ErrorCode;
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

    public ProcWeighVo getWeighHeadInfo(Long workProcId){
        return procWeighMapper.getWeighHeadInfo(workProcId);
    }
    /**
     * 칭량 상세 조회
     * @param vo
     * @return
     */
    public WeighInfoVo getWeighInfo(ProcWeighVo vo){
        WeighInfoVo info = new WeighInfoVo();

        info.setProcWeigh(this.getWeighHeadInfo(vo.getWorkProcId()));
        List<ProcWeighBomVo> recipeList = null;

        if (procWeighMapper.checkWeighCnt(vo.getWorkProcId()) > 0 ){
            recipeList = procWeighMapper.getRealBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }else{
            recipeList = procWeighMapper.getBomWeighList(vo.getWorkProcId(), vo.getItemCd());
        }
        info.setWeightBomList(recipeList);

        return  info;
    }

    public Long saveWeighInfo(WeighInfoVo vo) {
        ProcWeighVo mst = vo.getProcWeigh();
        List<ProcWeighBomVo> recipeList = vo.getWeightBomList();
        String userId = UserUtil.getUserId();

        if (procWeighMapper.updateProcWeigh(mst) <=0  ){
            throw new BusinessException(ErrorCode.FAIL_UPDATED);
        }

        int cntWeigh = procWeighMapper.checkWeighCnt(mst.getWorkProcId());

        for(ProcWeighBomVo item : recipeList ){
            item.setWorkProcId(mst.getWorkProcId());
            item.setUserId(userId);
            if(cntWeigh <= 0) {
                if (procWeighMapper.insertWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_CREATED);
                }
            }else{
                item.setUserId(userId);
                if (procWeighMapper.updateWeighRecipe(item) <=0  ){
                    throw new BusinessException(ErrorCode.FAIL_UPDATED);
                }
            }
        }
        return mst.getWorkProcId();
    }


}
