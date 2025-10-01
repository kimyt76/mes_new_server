package com.jct.mes_new.biz.lab.vo;

import lombok.Data;

import java.util.List;

@Data
public class RecipeRequestVo {

    private RecipeVo recipeInfo;
    private List<RecipeDetailVo> recipeList;
}
