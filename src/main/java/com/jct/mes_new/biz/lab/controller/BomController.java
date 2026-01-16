package com.jct.mes_new.biz.lab.controller;


import com.jct.mes_new.biz.lab.service.BomService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/bom")
public class BomController {

    private final BomService bomService;



    @PostMapping("/getBomList")
    public List<BomVo> getBomList(@RequestBody BomVo bomVo) {
        return bomService.getBomList(bomVo);
    }
    @PostMapping("/getBomMatList")
    public List<BomVo> getBomMatList(@RequestBody BomVo bomVo) {
        return bomService.getBomMatList(bomVo);
    }

    @GetMapping("/getItemBomList/{id}")
    public List<BomRecipeVo> getItemBomList(@PathVariable("id") String itemCd) {
        return bomService.getItemBomList(itemCd);
    }

    @PostMapping("/getItemsBomList")
    public Map<String, Object> getItemsBomList(@RequestBody Map<String, String> map) {
        Map<String, Object> result = new HashMap<>();
        result = bomService.getItemsBomList(map);

        return result;
    }

    @GetMapping("/getBomInfo/{id}")
    public BomRequestVo getBomInfo(@PathVariable("id") String bomId) {
        BomRequestVo req = bomService.getBomInfo(bomId);
        return req;
    }

    @GetMapping("/getProductTypeInfo/{id}")
    public BomVo getProductTypeInfo(@PathVariable("id") String prodCd) {
        return bomService.getProductTypeInfo(prodCd);
    }

    @PostMapping("/saveBomInfo")
    public ResponseEntity<?> saveBomInfo(@RequestBody BomRequestVo request ) throws Exception {
        try {
            BomVo bomInfo = request.getBomInfo();
            List<BomRecipeVo> bomRecipeList = request.getBomRecipeList();
            List<BomProcVo> bomProcList = request.getBomProcList();

            String result = bomService.saveBomInfo(bomInfo, bomRecipeList, bomProcList);

            Map<String, String> response = Map.of("message", result);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage(), 400));
        }
    }

    @PostMapping("/saveBomVerInfo")
    public ResponseEntity<?> saveBomVerInfo(@RequestBody BomRequestVo request ) throws Exception {
        try {
            BomVo bomInfo = request.getBomInfo();
            List<BomRecipeVo> bomRecipeList = request.getBomRecipeList();
            List<BomProcVo> bomProcList = request.getBomProcList();

            String result = bomService.saveBomVerInfo(bomInfo, bomRecipeList, bomProcList);

            Map<String, String> response = Map.of("message", result);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage(), 400));
        }
    }



}
