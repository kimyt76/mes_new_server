package com.jct.mes_new.biz.base.mapper;

import com.jct.mes_new.biz.base.vo.DailyMgmtVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DailyMgmtMapper {
    DailyMgmtVo selectDailyReportInfo(Long dailyId);

    List<DailyMgmtVo> selectDailyMgmtList(Long dailyId);
}
