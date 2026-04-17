package com.jct.mes_new.biz.proc.mapper;

import com.jct.mes_new.biz.common.vo.SearchCommonVo;
import com.jct.mes_new.biz.proc.vo.*;
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


    /**
     * 작업수행정보 (코팅, 충전, 포장)
     */
    List<WorkRecodeVo> getWorkRecodeList(@Param("workProcId") Long workProcId);

    /**
     * 공정별 투입자재 정보 조회
     * @param procCd
     * @param workProcId
     * @return
     */
    List<ProcUseInfoVo> getProdList(String procCd, Long workProcId);
}
