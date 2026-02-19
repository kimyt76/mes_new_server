package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcWeighMapper {
    List<ProcWeighVo> getWeighList(ProcWeighVo vo);

    int checkWeighCnt(String workProcId);

    ProcWeighVo getWeighHeadInfo(String workProcId);

    List<ProcWeighVo.weigh> getRealBomWeighList(String workProcId, String itemCd);

    List<ProcWeighVo.weigh> getBomWeighList(String workProcId, String itemCd);
}
