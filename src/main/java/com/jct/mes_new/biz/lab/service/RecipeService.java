package com.jct.mes_new.biz.lab.service;

import com.jct.mes_new.biz.lab.vo.*;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    List<RecipeVo> getRecipeList(RecipeVo recipeVo);

    RecipeRequestVo getNewMaterialInfo(String recipeId);

    String saveRecipeInfo(RecipeVo recipeInfo, List<RecipeDetailVo> recipeList);

    Map<String, Object> getProdInfo(String recipeId);

    List<AllIngredientVo> allIngredientInList(String recipeId);

    List<AllIngredientVo> allIngredientOutList(String recipeId);

    Map<String, Object> getProdTypeInfo(String prodTypeName);

}
