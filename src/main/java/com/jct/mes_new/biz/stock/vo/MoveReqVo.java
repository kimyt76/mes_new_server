package com.jct.mes_new.biz.stock.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MoveReqVo {

    private Long moveReqId;
    private Long moveReqItemId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveReqDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate moveRegDate;

    private int regSeq;
    private int seq;

    private String areaCd;
    private String srcStorageCd;
    private String tarStorageCd;
    private String managerId;
    private String moveStatus;

    private String etc;
    private String memo;
    private String userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate strDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Long worProcId;


}
