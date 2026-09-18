package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LaborCostsMapper {
    List<DailyLaborCostVo> getDailyLaborCostList(DailyLaborCostVo vo);

    List<DailyLaborCostVo> getProcList(String prc002, String number);
}
