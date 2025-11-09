package com.jct.mes_new.biz.lab.mapper;

import com.jct.mes_new.biz.lab.vo.AllIngredientVo;
import com.jct.mes_new.biz.lab.vo.NewMaterialVo;
import com.jct.mes_new.biz.lab.vo.RecipeDetailVo;
import com.jct.mes_new.biz.lab.vo.RecipeVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RecipeMapper {

    List<RecipeVo> getRecipeList(RecipeVo recipeVo);

    int saveRecipeInfo(RecipeVo recipeInfo);

    int saveRecipeList(RecipeDetailVo recipe);

    RecipeVo getRecipeInfo(String recipeId);

    List<RecipeDetailVo> getRecipeDtlList(String recipeId);

    void deleteRecipeList(String recipeId);

    Map<String, Object> getProdInfo(String recipeId);

    List<AllIngredientVo> allIngredientInList(String recipeId);

    List<AllIngredientVo> allIngredientOutList(String recipeId);

    Map<String, Object> getProdTypeInfo(String prodTypeName);

    List<RecipeVo> getProdTypeList();
}

