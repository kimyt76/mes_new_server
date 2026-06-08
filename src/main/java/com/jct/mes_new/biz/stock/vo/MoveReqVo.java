package com.jct.mes_new.biz.stock.vo;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MoveReqVo {

    private Long moveReqId;
    private Long worProcId;


    private LocalDate moveReqDate;
    private int seq;
    private String areaCd;
    private String srcStorageCd;
    private String tarStorageCd;
    private String managerId;
    private String etc;



}
