package com.jct.mes_new.biz.lab.controller;

import com.jct.mes_new.biz.lab.service.NewMaterialService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/newMaterial")
public class NewMaterialController {

    private final NewMaterialService newMaterialService;

    @PostMapping("/getNewMaterialList")
    public List<NewMaterialVo> getNewMaterialList (@RequestBody NewMaterialVo vo){
        return newMaterialService.getNewMaterialList(vo);
    }

    @GetMapping("/getNewMaterialInfo/{id}")
    public NewMaterialRequestVo getNewMaterialInfo (@PathVariable("id") String newMaterialCd){
        return newMaterialService.getNewMaterialInfo(newMaterialCd);
    }

    @GetMapping("/getNewMaterialListMapping/{id}")
    public List<IngredientVo> getNewMaterialListMapping (@PathVariable("id") String newMaterialCd){
        return newMaterialService.getNewMaterialListMapping(newMaterialCd);
    }

    @PostMapping("/saveNewMaterialInfo")
    public ResponseEntity<?> saveNewMaterialInfo(@RequestBody NewMaterialRequestVo request ) throws Exception {
        try {
            NewMaterialVo newMaterialInfo = request.getNewMaterialInfo();
            List<IngredientVo> materialMappingList = request.getMaterialMappingList();

            String result = newMaterialService.saveNewMaterialInfo(newMaterialInfo, materialMappingList);

            Map<String, String> response = Map.of("newMaterialCd", result);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("저장에 실패했습니다.", 400));
        }
    }

    @PostMapping("/saveNewMaterialMapping")
    public ResponseEntity<?> saveNewMaterialMapping(@RequestBody NewMaterialRequestVo request ) throws Exception {
        try {
            NewMaterialVo newMaterialInfo = request.getNewMaterialInfo();
            List<IngredientVo> materialMappingList = request.getMaterialMappingList();

            String result = newMaterialService.saveNewMaterialMapping(newMaterialInfo, materialMappingList);

            Map<String, String> response = Map.of("newMaterialCd", result);

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage(), 400));
        }
    }


}
