package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalTime;

@Data
public class WorkRecodeVo {

    private Long workRecodeId;
    private Long workProcId;
    private String itemCd;
    private String workDate;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private Integer workerCnt;
    private BigDecimal useQty;
    private BigDecimal prodQty;

    private String userId;

}
