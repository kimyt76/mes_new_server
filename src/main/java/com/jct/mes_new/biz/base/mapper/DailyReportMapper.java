package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DailyReportMapper {

    List<DailyReportVo> getM1DailyReportList(DailyReportVo vo);
    List<DailyReportVo> getM2DailyReportList(DailyReportVo vo);
    List<DailyReportVo> getM0DailyReportList(DailyReportVo vo);
    List<DailyReportVo> getLaborCostList(DailyReportVo vo);

    int insertDailyReportMst(DailyReportVo mst);

    List<DailyReportVo> getInList(DailyReportVo report);
    List<DailyReportVo> getProdList();
    List<DailyReportVo> getUseList();

    DailyReportVo getDailyReportMst(Long dailyId);
    void updateDailyReportEndYn(DailyReportVo vo);
    List<DailyReportVo> getUseM2List();


    List<DailyLaborCostVo> getProcList(String prc001, String number);
}
