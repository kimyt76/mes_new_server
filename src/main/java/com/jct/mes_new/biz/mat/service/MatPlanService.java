package com.jct.mes_new.biz.mat.service;

import com.jct.mes_new.biz.mat.vo.MatPlanVo;

import java.util.List;

public interface MatPlanService {
    List<MatPlanVo> getMatPlanList(MatPlanVo vo);

    List<MatPlanVo> getMatPlanDetailList(String matPlanId);

    void saveMatPlanList(List<MatPlanVo> voList);
}
