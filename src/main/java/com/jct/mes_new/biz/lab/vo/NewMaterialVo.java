package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class NewMaterialVo {

    private String newMaterialId;
    private String newMaterialCd;
    private String itemCd;
    private String materialName;
    private BigDecimal inPrice;
    private String customerCd;
    private String customerName;
    private String mtrName;

    private BigInteger rowNum;
    private String regName;
    private String regDate;
    private String userId;
}
