package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LaborCostsMapper {
    List<DailyLaborCostVo> getDailyLaborCostList(DailyReportVo vo);

    List<DailyLaborCostVo> getLaborCostProcList(@Param("procCd") String procCd, @Param("dailyId") Long dailyId);

    int insertLaborCost(DailyLaborCostVo item);
    int updateLaborCost(DailyLaborCostVo item);

    List<DailyLaborCostVo> getLaborCostList();
}
