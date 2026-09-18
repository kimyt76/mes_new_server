package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class DailyReportRequestVo {

    DailyReportVo dailyReportInfo;
    List<DailyReportVo> inList;        /* 입고*/
    List<DailyReportVo> returnList;         /* 반품*/
    List<DailyReportVo> discardList;        /* 불량 폐기*/
    List<DailyReportVo> prodList;          /* 제품 사용량*/
    List<DailyReportVo> useList;          /* 원료사용량*/
    List<DailyReportVo> ospList;          /* 외부반출*/

    private List<Long> deleteInIds;
    private List<Long> deleteReturnIds;
    private List<Long> deleteDiscardIds;
    private List<Long> deleteProdIds;
    private List<Long> deleteUseIds;
    private List<Long> deleteOspIds;

    /* 완제품*/
    List<DailyReportVo> outList;          /* 외부생산내역*/
    List<DailyReportVo> outExpenseList;          /* 외부생산 비용*/
    List<DailyReportVo> shipmentList;          /* 출하*/

    private List<Long> deleteOutIds;
    private List<Long> deleteOutExpenseIds;
    private List<Long> deleteShipmentIds;
    







}
