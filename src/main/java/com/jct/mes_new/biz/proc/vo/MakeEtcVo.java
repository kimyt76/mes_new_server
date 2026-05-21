package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MakeEtcVo {

    private Long makeEtcId;
    private Long workProcId;
    private String itemCd;
    private String matProc;         //공정
    private String manipulate;      //조건조작
    private String processTime;     //공정 시간
    private String bomVer;
    private String note;
    private String significant;
    private String maker;
    private String confirmer;

    private Integer filterCnt;
    private String filterType;
    private String filterDamage;
    private String substanceCheck;

    private String userId;


}
