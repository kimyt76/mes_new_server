package com.jct.mes_new.biz.mat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class MatPlanVo {

    public String matPlanId;

    public String strDate;
    public String toDate;

    public String matRegDate;
    public String poNo;
    public String itemCd;
    public String itemName;
    public BigDecimal qty;
    public BigDecimal theoryMakeQty;
    public BigDecimal matInstrQty;
    public BigDecimal matCompQty;
    public BigDecimal resMatQty;
    public String matPlanDate;
    public String etc;
    public String matInYn;
    public String endYn;
    public String useYn;
    public String userId;

}
