package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class RecipeDetailVo {

    private BigDecimal recipeItemId;
    private String recipeId;
    private String itemCd;
    private String itemName;
    private String phase;
    private BigDecimal content;
    private BigDecimal inPrice;
    private BigDecimal unitPrice;
    private BigInteger sortDist;
    private String userId;
}
