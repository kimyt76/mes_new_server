package com.jct.mes_new.biz.base.service;

import com.jct.mes_new.biz.base.vo.DailyLaborCostVo;
import com.jct.mes_new.biz.base.vo.DailyReportRequestVo;
import com.jct.mes_new.biz.base.vo.DailyReportVo;
import com.jct.mes_new.biz.base.vo.LaborCostRequestVo;

import java.util.List;

public interface DailyReportService {

    String updateDailyReportEndYn(DailyReportVo vo);

    /* 생산일보 원료*/
    List<DailyReportVo> getM1DailyReportList(DailyReportVo vo);
    DailyReportRequestVo getM1DailyReportInfo(Long dailyId);
    String saveDailyReportM1(DailyReportRequestVo vo);

    /* 생산일보 부자재*/
    List<DailyReportVo> getM2DailyReportList(DailyReportVo vo);
    DailyReportRequestVo getM2DailyReportInfo(Long dailyId);
    String saveDailyReportM2(DailyReportRequestVo vo);

    /* 생산일보 완제품*/
    List<DailyReportVo> getM0DailyReportList(DailyReportVo vo);
    DailyReportRequestVo getM0DailyReportInfo(Long dailyId);
    String saveDailyReportM0(DailyReportRequestVo vo);

    /* 생산일보 인건비*/
    List<DailyLaborCostVo> getDailyLaborCostList(DailyLaborCostVo vo);
    LaborCostRequestVo getLaborCostInfo(Long dailyId);

    byte[] downloadDailyReport(DailyReportVo vo);
}
