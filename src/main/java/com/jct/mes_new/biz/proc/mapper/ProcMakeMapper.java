package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcMakeVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighBomVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProcMakeMapper {

    List<ProcMakeVo> getMatList(ProcMakeVo vo);

    ProcMakeVo getMakeHeadInfo(long workProcId);

    List<ProcWeighBomVo> getRealBomMakeList(long workBatchId, String itemCd);

    List<ProcWeighBomVo> getBomMakeList(long workBatchId, String itemCd);
}
