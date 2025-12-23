package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class BomRecipeVo {

    private String bomItemId;
    private String bomId;
    private String phase;
    private String itemCd;
    private String realItemName;
    private String realItemCd;
    private String stdItemCd;
    private String stdItemName;
    private BigDecimal realContent;
    private BigDecimal stdContent;
    private BigInteger orderDist;
    private String etc;
    private BigInteger qty;

    private String itemName;
    private String bomVer;
    private String userId;
}
