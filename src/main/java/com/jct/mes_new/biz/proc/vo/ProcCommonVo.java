package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProcCommonVo {

    private BigInteger equipmentId;
    private String equipmentCd;
    private String equipmentName;

    private Long workOrderId;
    private Long workBatchId;
    private Long workProcId;
    private String storageCd;
    private String procStatus;
    private String batchStatus;
    private String managerId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;
    private String itemCd;
    private String procCd;

    private String workerName;
    private String etc;
    private String memo;
    private String areaCd;
    private String testNo;

    private String bagWeightName;
    private BigDecimal weight;

    private String userId;

}
