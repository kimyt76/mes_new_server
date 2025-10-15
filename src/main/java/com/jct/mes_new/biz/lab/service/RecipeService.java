package com.jct.mes_new.biz.lab.service;

import com.jct.mes_new.biz.lab.vo.NewMaterialVo;
import com.jct.mes_new.biz.lab.vo.RecipeDetailVo;
import com.jct.mes_new.biz.lab.vo.RecipeRequestVo;
import com.jct.mes_new.biz.lab.vo.RecipeVo;

import java.util.List;
import java.util.Map;

public interface RecipeService {
    List<RecipeVo> getRecipeList(RecipeVo recipeVo);

    RecipeRequestVo getNewMaterialInfo(String recipeId);

    String saveRecipeInfo(RecipeVo recipeInfo, List<RecipeDetailVo> recipeList);
}
