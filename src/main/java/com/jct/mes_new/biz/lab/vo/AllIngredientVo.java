package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class AllIngredientVo {

    private BigInteger rowNum;
    private String recipeId;
    private String krIngredientName;
    private String enIngredientName;
    private String cnIngredientName;

    private BigDecimal content;
    private BigDecimal composition;
    private BigDecimal InFormula;
    private String reference;
    private String casNo;
    private String functionName;
}
