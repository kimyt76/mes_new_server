
package com.jct.mes_new.biz.base.vo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class DailyMgmtVo {

    private Long dailyId;

    private String typeCd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dailyDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    // 전일재고 수량
    private BigDecimal rawPrevStockQty;
    // 전일재고 금액
    private BigDecimal rawPrevStockAmt;
    // 입고 수량
    private BigDecimal rawReceiptQty;
    // 입고 금액
    private BigDecimal rawReceiptAmt;
    // 반품 수량
    private BigDecimal rawReturnQty;
    // 반품 금액
    private BigDecimal rawReturnAmt;
    // 불량 폐기 수량
    private BigDecimal rawDefectQty;
    // 불량 폐기 금액
    private BigDecimal rawDefectAmt;
    // 외주가공비 수량
    private BigDecimal rawOutsourceQty;
    // 외주가공비 금액
    private BigDecimal rawOutsourceAmt;
    // 사용량 수량
    private BigDecimal rawUsageQty;
    // 사용량 금액
    private BigDecimal rawUsageAmt;
    // 외주 반출 수량
    private BigDecimal rawOutboundQty;
    // 외주 반출 금액
    private BigDecimal rawOutboundAmt;
    // 전일재고 수량
    private BigDecimal subPrevStockQty;
    // 전일재고 금액
    private BigDecimal subPrevStockAmt;
    // 입고 수량
    private BigDecimal subReceiptQty;
    // 입고 금액
    private BigDecimal subReceiptAmt;
    // 반품 수량
    private BigDecimal subReturnQty;
    // 반품 금액
    private BigDecimal subReturnAmt;
    // 불량 폐기 수량
    private BigDecimal subDefectQty;
    // 불량 폐기 금액
    private BigDecimal subDefectAmt;
    // 외주가공비 수량
    private BigDecimal subOutsourceQty;
    // 외주가공비 금액
    private BigDecimal subOutsourceAmt;
    // 사용량 수량
    private BigDecimal subUsageQty;
    // 사용량 금액
    private BigDecimal subUsageAmt;
    // 외주 반출 수량
    private BigDecimal subOutboundQty;
    // 외주 반출 금액
    private BigDecimal subOutboundAmt;
    // 전일재고 수량
    private BigDecimal productPrevStockQty;
    // 전일재고 금액
    private BigDecimal productPrevStockAmt;
    // 생산 수량
    private BigDecimal productProdQty;
    // 생산 금액
    private BigDecimal productProdAmt;
    // 외주 생산 수량
    private BigDecimal productOutsourceProdQty;
    // 외주 생산 금액
    private BigDecimal productOutsourceProdAmt;
    // 불량 폐기 수량
    private BigDecimal productDefectQty;
    // 불량 폐기 금액
    private BigDecimal productDefectAmt;
    // 외주가공비 수량
    private BigDecimal productOutsourceQty;
    // 외주가공비 금액
    private BigDecimal productOutsourceAmt;
    // 출하 수량
    private BigDecimal productShipmentQty;
    // 출하 금액
    private BigDecimal productShipmentAmt;
    // 반품 수량
    private BigDecimal productReturnQty;
    // 반품 금액
    private BigDecimal productReturnAmt;
    private Integer mgmtDayMaleCnt;
    private Integer mgmtDayFemaleCnt;
    private BigDecimal mgmtDayWorkHour;
    private BigDecimal mgmtDayHourlyCost;
    private Integer regularDayMaleCnt;
    private Integer regularDayFemaleCnt;
    private BigDecimal regularDayWorkHour;
    private BigDecimal regularDayHourlyCost;
    private Integer dailyMaleDayMaleCnt;
    private Integer dailyMaleDayFemaleCnt;
    private BigDecimal dailyMaleDayWorkHour;
    private BigDecimal dailyMaleDayHourlyCost;
    private Integer dailyFemaleDayMaleCnt;
    private Integer dailyFemaleDayFemaleCnt;
    private BigDecimal dailyFemaleDayWorkHour;
    private BigDecimal dailyFemaleDayHourlyCost;
    private Integer mgmtOvertimeMaleCnt;
    private Integer mgmtOvertimeFemaleCnt;
    private BigDecimal mgmtOvertimeWorkHour;
    private Integer regularOvertimeMaleCnt;
    private Integer regularOvertimeFemaleCnt;
    private BigDecimal regularOvertimeWorkHour;
    private Integer dailyMaleOvertimeMaleCnt;
    private Integer dailyMaleOvertimeFemaleCnt;
    private BigDecimal dailyMaleOvertimeWorkHour;
    private Integer dailyFemaleOvertimeMaleCnt;
    private Integer dailyFemaleOvertimeFemaleCnt;
    private BigDecimal dailyFemaleOvertimeWorkHour;
    private Integer mgmtNightMaleCnt;
    private Integer mgmtNightFemaleCnt;
    private BigDecimal mgmtNightWorkHour;
    private Integer regularNightMaleCnt;
    private Integer regularNightFemaleCnt;
    private BigDecimal regularNightWorkHour;
    private Integer dailyMaleNightMaleCnt;
    private Integer dailyMaleNightFemaleCnt;
    private BigDecimal dailyMaleNightWorkHour;
    private BigDecimal dailyMaleNightHourlyCost;
    private Integer dailyFemaleNightMaleCnt;
    private Integer dailyFemaleNightFemaleCnt;
    private BigDecimal dailyFemaleNightWorkHour;
    private BigDecimal dailyFemaleNightHourlyCost;
    // E43 소모품
    private BigDecimal suppliesDirectCost;
    // E44 식대
    private BigDecimal mealDirectCost;
    // E45 기타(전력비외)
    private BigDecimal etcDirectCost;
    // J46 기타판관 경비
    private BigDecimal adminEtcIndirectCost;
    // J47 감가상각비
    private BigDecimal depreciationIndirectCost;
    // J48 금융비용
    private BigDecimal financeIndirectCost;
    // 간접인건비
    private BigDecimal indirectLaborCost;

    private String regId;
    private String userId;
}
