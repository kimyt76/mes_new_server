package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyReportVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface M0DailyReportMapper {

    List<DailyReportVo> getDailyReportList(DailyReportVo report);

    void deleteDailyReport(List<Long> deleteIds);

    int insertDailyReportM0(DailyReportVo item);
    int updateDailyReportM0(DailyReportVo item);
}
