package com.jct.mes_new.biz.lab.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jct.mes_new.biz.lab.service.RecipeService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/recipe")
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping("/getRecipeList")
    public List<RecipeVo> getRecipeList(@RequestBody RecipeVo recipeVo) {
        return recipeService.getRecipeList(recipeVo);
    }

    @GetMapping("/getRecipeInfo/{id}")
    public RecipeRequestVo getNewMaterialInfo (@PathVariable("id") String recipeCd) {
        return recipeService.getNewMaterialInfo(recipeCd);
    }

    @PostMapping("/saveRecipeInfo")
    public ResponseEntity<?> saveRecipeInfo(@RequestBody RecipeRequestVo request ) throws Exception {

        try {
            RecipeVo recipeInfo = request.getRecipeInfo();
            List<RecipeDetailVo> recipeList = request.getRecipeList();

            String result = recipeService.saveRecipeInfo(recipeInfo, recipeList);

            Map<String, String> response = Map.of("recipe_id", result);

            return ResponseEntity.ok(ApiResponse.success(response));
            //return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("저장에 실패했습니다.", 400));
        }
    }

}
