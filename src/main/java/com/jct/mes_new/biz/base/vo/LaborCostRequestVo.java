package com.jct.mes_new.biz.base.vo;

import lombok.Data;

import java.util.List;

@Data
public class LaborCostRequestVo {

    DailyReportVo dailyReportInfo;
    List<DailyLaborCostVo> weighList;       /* 칭량*/
    List<DailyLaborCostVo> matList;         /* 제조*/
    List<DailyLaborCostVo> coatingList;     /* 코팅*/
    List<DailyLaborCostVo> chargeList;      /* 충전*/
    List<DailyLaborCostVo> packingList;     /* 포장*/

    List<DailyLaborCostVo> costList;        /* 인건비용 */

    private List<Long> deleteWeighIds;
    private List<Long> deleteMatIds;
    private List<Long> deleteCoatingIds;
    private List<Long> deleteChargeIds;
    private List<Long> deletePackingIds;
}
