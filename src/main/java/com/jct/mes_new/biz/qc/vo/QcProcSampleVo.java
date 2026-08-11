package com.jct.mes_new.biz.qc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class QcProcSampleVo {

    private Long  qcProcTestMstId;
    private Long  qcProcTestSampleId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private String sampleDate;
    private Integer qty1;
    private Integer qty2;
    private Integer qty3;
    private Integer qty4;
    private Integer qty5;
    private Integer qty6;
    private Integer qty7;
    private Integer qty8;
    private Integer qty9;

    private String userId;

}
