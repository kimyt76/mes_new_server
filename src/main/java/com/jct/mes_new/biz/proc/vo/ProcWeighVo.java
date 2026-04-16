package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.beans.BeanInfo;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProcWeighVo {
    private Long  weighId;
    private Long  workProcId;
    private Long  workBatchId;
    private Long  workOrderId;
    private Long  weighInvId;
    private Long tranId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate procOrderDate;    //지시일
    private String poNo;
    private String workOrderDateSeq;
    private String itemTypeCd;
    private String itemCd;
    private String itemName;
    private String makeNo;
    private String lotNo;
    private BigDecimal orderQty;
    private BigDecimal weighQty;
    private String batchStatus;
    private String procStatus;
    private String areaCd;
    private String storageCd;
    private String storageName;
    private String etc;
    private String memo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;    //칭량일

    private String testNo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    private String procCd;
    private LocalDateTime workStartTime;
    private LocalDateTime workEndTime;
    private String managerId;
    private String tranYn;
    private String endYn;


    private String userId;













}