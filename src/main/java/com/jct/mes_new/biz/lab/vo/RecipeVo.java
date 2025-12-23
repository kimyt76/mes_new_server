package com.jct.mes_new.biz.lab.vo;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jct.mes_new.config.util.DateStringToYmdDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RecipeVo {

    private String recipeId;
    private String prodName;
    @JsonDeserialize(using = DateStringToYmdDeserializer.class)
    private String regDate;
    private String labNo;
    private String clientId;
    private String managerId;
    private String prodType;
    private String itemCd;

    private BigDecimal rowNum;
    private String managerName;
    private String clientName;
    private String item_name;

    private String code;
    private String codeNm;

    private String userId;
}
