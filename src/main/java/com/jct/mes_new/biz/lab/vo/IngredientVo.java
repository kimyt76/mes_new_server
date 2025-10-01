package com.jct.mes_new.biz.lab.vo;

import lombok.Data;
import org.springframework.core.KotlinReflectionParameterNameDiscoverer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Data
public class IngredientVo {

    private String ingredientCode;
    private String ingredientDate;
    private String ingredientName;
    private String krIngredientName;
    private String enIngredientName;
    private String cnIngredientName;
    private String functionNm;
    private String functionCd;
    private String casNo;
    private String etc;
    private String regName;
    private String updName;
    private String updDate;

    private String type;

    private List<String> limitCountries;
    private List<String> bannedCountries;

    private String limitCountry;
    private String banCountry;
    private BigDecimal inContent;
    private BigDecimal outContent;


    private String newMaterialId;
    private String newMaterialCd;
    private BigInteger newDtlId;
    private String id;
    private String itemCd;
    private String userId;
}
