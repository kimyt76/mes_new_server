package com.jct.mes_new.biz.qc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QcProcTestVo {

    private Long  qcProcTestMstId;
    private Long  workBatchId;
    
    private String qcTestType; /* 공정검사 구분*/
    private String testType; /* 탭 구분*/

    private String testerId;
    private String testState;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String areaCd;
    private String itemName;
    private String itemCd;
    private String makeNo;
    private String lotNo;
    private String lotNo2;
    private String batchStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate chargingDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate packingDate;


    private String clientName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate prodDate;
    private String testerName;
    private String spec;
    private String etc;

    private String displayCapacity;
    private String workStartTime;
    private String workEndTime;
    private String chargingCnt;
    private String cappingRange;
    private String essenceStd;
    private String workFlow;

    private String userId;




}
