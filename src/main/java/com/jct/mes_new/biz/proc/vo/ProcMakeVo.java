package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProcMakeVo {
    private Long makeId;
    private Long weighId;
    private Long workProcId;
    private Long workBatchId;
    private Long workOrderId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate procOrderDate;    //지시일
    private String poNo;
    private String workOrderDateSeq;
    private String itemCd;
    private String itemName;
    private String makeNo;
    private String lotNo;
    private String lotNo2;
    private BigDecimal orderQty;
    private String batchStatus;
    private String procStatus;
    private String areaCd;
    private String storageCd;
    private String storageName;
    private String etc;
    private String memo;
    private String clientCd;
    private String clientId;
    private String clientName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;    //칭량일

    private String testNo;
    private String procCd;
    private String workStartTime;
    private String workEndTime;
    private String workEquipmentCd;
    private String managerId;


    private String userId;




}
