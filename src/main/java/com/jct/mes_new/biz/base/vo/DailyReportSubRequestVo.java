package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class DailyReportSubRequestVo {

    DailyReportVo dailyReportMst;
    List<DailyReportVo> inList;        /* 입고*/
    List<DailyReportVo> returnList;         /* 반품*/
    List<DailyReportVo> discardList;        /* 불량 폐기*/
    List<DailyReportVo> usageList;          /* 제품 사용량*/
    List<DailyReportVo> stockList;          /* 원료사용량*/
    List<DailyReportVo> ospList;          /* 외부반출*/

    private List<Long> deleteInIds;
    private List<Long> deleteReturnIds;
    private List<Long> deleteDiscardIds;
    private List<Long> deleteUsageIds;
    private List<Long> deleteStockIds;
    private List<Long> deleteOspIds;

    List<DailyReportVo> subUsageList;  /* 부자재 사용량*/

}
