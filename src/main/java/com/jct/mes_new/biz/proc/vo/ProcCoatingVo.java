package com.jct.mes_new.biz.proc.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProcCoatingVo {

    private String workProcId;
    private String workOrderId;

    private String areaCd;
    private String areaName;
    private String poNo;
    private String procOrderDate;
    private String matNo;
    private String lotNo;
    private String itemCd;
    private String itemName;
    private BigDecimal orderQty;
    private String moveReqYn;
    private String processState;
    private String procStatus;
    private String storageName;
    private String coatingStat;

    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String strDate;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String toDate;

}
