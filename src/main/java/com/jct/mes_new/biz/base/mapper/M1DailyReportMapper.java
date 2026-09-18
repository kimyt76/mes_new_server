package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyReportVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface M1DailyReportMapper {

    List<DailyReportVo> getDailyReportList(DailyReportVo report);
    List<DailyReportVo> getProdList(DailyReportVo report);
    List<DailyReportVo> getUseList(DailyReportVo report);

    void deleteDailyReport(List<Long> deleteInIds);
    void deleteDailyReportProd(List<Long> deleteProdIds);
    void deleteDailyReportUse(List<Long> deleteUseIds);

    int insertDailyReportProd(DailyReportVo usa);
    int updateDailyReportProd(DailyReportVo usa);

    int insertDailyReportUse(DailyReportVo stock);
    int updateDailyReportUse(DailyReportVo stock);

    int insertDailyReportM1(DailyReportVo item);
    int updateDailyReportM1(DailyReportVo item);

}
