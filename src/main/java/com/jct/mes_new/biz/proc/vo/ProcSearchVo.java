package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProcSearchVo {
    private String strDate;
    private String endDate;

    private String procCd;
    private String areaCd;
    private String storageCd;
    private String makeNo;
    private String itemCd;
    private String itemName;
    private String procStatus;
    private String batchStatus;
    private String clientName;


}
