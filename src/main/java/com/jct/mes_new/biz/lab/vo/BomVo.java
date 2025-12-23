package com.jct.mes_new.biz.lab.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigInteger;

@Data
public class BomVo {

    private String bomId;
    private String itemCd;
    private String itemName;
    private String clientId;
    private String clientName;
    private String managerId;
    private String managerName;
    private String labNo;
    private String prodType;
    private String prodTypeName;
    private String bomVer;
    private String usage;
    private String caution;
    private String significant;
    private String note;
    private BigInteger qty;
    private String approvalState;
    private String defaultYn;
    private String etc;
    private String useYn;
    private String itemCnt;


    private String asBomId;
    private String itemTypeCd;
    private String unit;
    private String userId;
}
