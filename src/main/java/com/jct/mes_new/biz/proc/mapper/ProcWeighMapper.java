package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import com.jct.mes_new.biz.proc.vo.WeighInfoVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcWeighMapper {
    List<ProcWeighVo> getWeighList(ProcWeighVo vo);

    int checkWeighCnt(Long workProcId);

    ProcWeighVo getWeighHeadInfo(Long workProcId);

    List<ProcWeighBomVo> getRealBomWeighList(Long workProcId, String itemCd);

    List<ProcWeighBomVo> getBomWeighList(Long workProcId, String itemCd);

    int insertWeighRecipe(ProcWeighBomVo item);

    int updateWeighRecipe(ProcWeighBomVo item);

    int updateProcWeigh(ProcWeighVo vo);

    int getWeighInfoCnt(long workProcId);

    int startProcWeigh(ProcWeighVo vo);

    void deleteWeighList(long workProcId, List<Long> deleteWeighItems);

    int insertWeighInvItem(ProcWeighVo item);

    int updateWeighInvItem(ProcWeighVo item);

    List<ProcWeighVo> getStockTestNoList(ProcWeighVo vo);

    List<ProcWeighVo> getWeighInvInfo(Long workProcId);
}
