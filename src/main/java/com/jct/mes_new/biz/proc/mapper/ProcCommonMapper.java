package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcWeighVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcCommonMapper {
    List<ProcCommonVo> getWorkerList(@Param("areaCd") String areaCd, @Param("procCd") String procCd );

    List<ProcCommonVo> getEquipmentList(String storageCd);

    int updateProcStatus(ProcCommonVo vo);

    int updateBatchStatus(ProcCommonVo vo);

    List<ProcCommonVo> getBagWeightList();

    Long updateProcStatusComplete(ProcWeighVo vo);

    Long getWorkProcId(long workBatchId, String procCd);
}
