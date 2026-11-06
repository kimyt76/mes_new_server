package com.jct.mes_new.biz.proc.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class MatConditionVo {

    private Long matConditionId;
    private Long workProcId;

    private String phase;
    private BigDecimal temperature;
    private BigDecimal homo;
    private BigDecimal disper;
    private BigDecimal paddle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String itemCd;

    private String userId;
}
