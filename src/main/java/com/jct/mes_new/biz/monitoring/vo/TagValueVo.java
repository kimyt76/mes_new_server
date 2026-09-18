package com.jct.mes_new.biz.monitoring.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TagValueVo {
    private Long tagValueId;
    private String tagCd;
    private BigDecimal measureValue;
    private String stringValue;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime measureTime;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime regTime;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime strDate;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;
}
