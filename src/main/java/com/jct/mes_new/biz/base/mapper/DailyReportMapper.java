package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import com.jct.mes_new.biz.base.vo.DailyMgmtVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DailyReportMapper {

    List<DailyReportVo> getM1DailyReportList(DailyReportVo vo);
    List<DailyReportVo> getM2DailyReportList(DailyReportVo vo);
    List<DailyReportVo> getM0DailyReportList(DailyReportVo vo);
    List<DailyReportVo> getLaborCostList(DailyReportVo vo);

    int insertDailyReportMst(DailyReportVo mst);
    void updateDailyReportMst(DailyReportVo mst);

    List<DailyReportVo> getInList(DailyReportVo report);
    List<DailyReportVo> getProdList(DailyReportVo vo);
    List<DailyReportVo> getUseList(DailyReportVo vo);

    DailyReportVo getDailyReportMst(Long dailyId);
    void updateDailyReportEndYn(DailyReportVo vo);
    List<DailyReportVo> getUseM2List(DailyReportVo vo);


    List<DailyLaborCostVo> getProcList(DailyLaborCostVo vo);


    int insertDailyMgmt(DailyMgmtVo vo);

    int updateDailyMgmt(DailyMgmtVo vo);

    List<DailyMgmtVo> getDailyMgmtList(DailyReportVo vo);

    DailyMgmtVo getDailyMgmtInfo(Long dailyId);
}
