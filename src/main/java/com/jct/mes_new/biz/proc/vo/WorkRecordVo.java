package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class WorkRecordVo {

    private Long workRecordId;
    private Long workProcId;
    private String itemCd;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;
    private LocalTime workStartTime;
    private LocalTime workEndTime;
    private Integer workerCnt;
    private BigDecimal useQty;
    private BigDecimal prodQty;

    private String userId;

}
