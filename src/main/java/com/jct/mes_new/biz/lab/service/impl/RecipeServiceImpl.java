package com.jct.mes_new.biz.lab.service.impl;

import com.jct.mes_new.biz.lab.mapper.RecipeMapper;
import com.jct.mes_new.biz.lab.service.RecipeService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.common.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeMapper recipeMapper;


    public List<RecipeVo> getRecipeList(RecipeVo recipeVo){
        return recipeMapper.getRecipeList(recipeVo);
    }

    public RecipeRequestVo getNewMaterialInfo(String recipeId){
        RecipeRequestVo vo = new RecipeRequestVo();

        vo.setRecipeInfo(recipeMapper.getRecipeInfo(recipeId));
        vo.setRecipeList(recipeMapper.getRecipeDtlList(recipeId));

        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String saveRecipeInfo(RecipeVo recipeInfo, List<RecipeDetailVo> recipeList){
        String recipe_id =recipeInfo.getRecipeId();
        try{
            if( recipe_id == null || recipe_id.isEmpty()){
                recipe_id = CommonUtil.generateUUID();
                recipeInfo.setRecipeId(recipe_id);
            }
            if( recipeMapper.saveRecipeInfo(recipeInfo) <= 0 ){
                throw new Exception("처방정보 저장에 실패했습니다.");
            }
            if(!recipeList.isEmpty()){
                recipeMapper.deleteRecipeList(recipe_id);

                for(RecipeDetailVo recipe : recipeList){
                    recipe.setRecipeId(recipe_id);
                    recipe.setUserId(recipeInfo.getUserId());
                    if( recipeMapper.saveRecipeList(recipe) <= 0 ){
                        throw new Exception("처방정보 저장에 실패했습니다.");
                    }
                }

//                if( recipeMapper.saveRecipeList(recipeList) <= 0 ){
//                    throw new Exception("처방정보 저장에 실패했습니다.");
//                }
            }
        }catch(Exception e){
            throw new RuntimeException("저장에 실패했습니다.: " + e.getMessage(), e);
        }
        return recipe_id;
    }

    public Map<String, Object> getProdInfo(String recipeId){

        return recipeMapper.getProdInfo(recipeId);
    }

    public List<AllIngredientVo> allIngredientInList(String recipeId){
        return recipeMapper.allIngredientInList(recipeId);
    }

    public List<AllIngredientVo> allIngredientOutList(String recipeId){
        return  recipeMapper.allIngredientOutList(recipeId);
    }

    public Map<String, Object> getProdTypeInfo(String prodTypeName){
        return recipeMapper.getProdTypeInfo(prodTypeName);
    }
}
