package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.lab.vo.NewMaterialVo;
import com.jct.mes_new.biz.lab.vo.RecipeDetailVo;
import com.jct.mes_new.biz.lab.vo.RecipeVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecipeMapper {

    List<RecipeVo> getRecipeList(RecipeVo recipeVo);

    int saveRecipeInfo(RecipeVo recipeInfo);

    int saveRecipeList(RecipeDetailVo recipe);

    RecipeVo getRecipeInfo(String recipeId);

    List<RecipeDetailVo> getRecipeDtlList(String recipeId);

    void deleteRecipeList(String recipeId);
}

