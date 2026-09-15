package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.*;
import com.jct.mes_new.biz.work.vo.WorkOrderInfoVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcMakeMapper {

    List<ProcWeighBomVo> getRealBomMakeList(@Param("workProcId") Long workProcId, @Param("itemCd") String itemCd);

    int updateProcMake(ProcMakeVo vo);

    Long getWeighQty(Long weighId);

    int updateMakeRecipe(ProcWeighBomVo item);

    MakeEtcVo getMakeEtcInfo(Long workProcId);

    int updateMakeEtc(MakeEtcVo makeEtcInfo);

    int insertMakeEtcInfo(MakeEtcVo vo);

    int updateMakeProcComplete(ProcMakeVo vo);

    int startProcMake(ProcMakeVo vo);

    int updateProdQty(WorkOrderInfoVo mst);

    Long getWorkProcId(Long workBatchId, String procCd);

    List<WorkOrderInfoVo> getMatProcCondList(ProcSearchVo vo);

    int conditionCnt(Long workProcId);

    List<MatConditionVo> getRecipeCondition(String itemCd);
    List<MatConditionVo> getMatCondition(Long workProcId);

    ProcMakeVo getMakeInfo(Long weighId);

    void updateMakeYn(Long weighId);
}
