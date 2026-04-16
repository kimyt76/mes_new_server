package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.MakeInfoVo;
import com.jct.mes_new.biz.proc.vo.ProcCommonVo;
import com.jct.mes_new.biz.proc.vo.ProcTranVo;
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

    Long getWorkProcId(long workBatchId, String procCd);

    int updateProcEtc(ProcCommonVo vo);

    List<ProcTranVo> getProcTranList(SearchCommonVo vo);
}
