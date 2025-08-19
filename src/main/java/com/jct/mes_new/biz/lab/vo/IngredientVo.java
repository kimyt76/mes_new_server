package com.jct.mes_new.biz.lab.vo;

import lombok.Data;
import org.springframework.core.KotlinReflectionParameterNameDiscoverer;

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
    private String regNm;
    private String updNm;
    private String updDate;

    private String type;

    private List<String> limitCountries;
    private List<String> bannedCountries;

    private String userId;
}
