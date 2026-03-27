package com.jct.mes_new.biz.qc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QcProcTestVo {

    private Long  qcProcTestMstId;
    private Long  workBatchId;
    private String qcProcTestType;
    private String testerId;
    private String testState;

    private String areaCd;
    private String itemName;
    private String itemCd;
    private String matNo;
    private String lotNo;
    private String lotNo2;
    private String batchStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate chargingDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate packingDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    private String clientName;
    private String spec;

    private String userId;

}
