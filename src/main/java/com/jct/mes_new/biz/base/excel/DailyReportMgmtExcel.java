package com.jct.mes_new.biz.base.excel;

import com.jct.mes_new.biz.base.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DailyReportMgmtExcel {

    private final DailyReportMapper dailyReportMapper;
    private final M0DailyReportMapper m0DailyReportMapper;
    private final M1DailyReportMapper m1DailyReportMapper;
    private final M2DailyReportMapper m2DailyReportMapper;
    private final LaborCostsMapper laborCostsMapper;





}
