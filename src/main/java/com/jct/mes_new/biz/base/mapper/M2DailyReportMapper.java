package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyReportVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface M2DailyReportMapper {

    List<DailyReportVo> getDailyReportList(DailyReportVo report);
    List<DailyReportVo> getUseList(DailyReportVo report);

    void deleteDailyReport(List<Long> deleteInIds);
    void deleteDailyReportUse(List<Long> deleteUseIds);

    int insertDailyReportUse(DailyReportVo use);
    int updateDailyReportUse(DailyReportVo use);

    int insertDailyReportM2(DailyReportVo item);
    int updateDailyReportM2(DailyReportVo item);
}
