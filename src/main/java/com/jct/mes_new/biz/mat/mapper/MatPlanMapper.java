package com.jct.mes_new.biz.mat.mapper;

import com.jct.mes_new.biz.mat.vo.MatPlanVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MatPlanMapper {
    List<MatPlanVo> getMatPlanList(MatPlanVo vo);

    int saveMatPlanList(MatPlanVo vo);
    int updateMatPlan(MatPlanVo vo);

    List<MatPlanVo> getMatPlanDetailList(@Param("matPlanId") String matPlanId);
}
