package com.jct.mes_new.biz.monitoring.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TagInfoVo {
    private Long tagInfoId;
    private String tagCd;
    private String tagName;
    private String dataType;
    private String gatherType;
    private String source;
    private String address;
    private String itemId;
    private Integer readSize;
    private Integer conversionFactor;
    private BigDecimal readValue;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime readTime;
    private String manageYn;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime strDate;

    @JsonFormat(timezone = "GMT+9", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime endDate;

}
