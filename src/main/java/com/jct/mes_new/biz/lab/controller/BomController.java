package com.jct.mes_new.biz.lab.controller;


import com.jct.mes_new.biz.lab.service.BomService;
import com.jct.mes_new.biz.lab.vo.*;
import com.jct.mes_new.config.common.MessageUtil;
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
    private final MessageUtil messageUtil;

    /**
     * BOM 리스트 조회
     * @param bomVo
     * @return
     */
    @PostMapping("/getBomList")
    public List<BomVo> getBomList(@RequestBody BomVo bomVo) {
        return bomService.getBomList(bomVo);
    }

    /**
     * 벌크/포장/완제품BOM목록
     * @param bomVo
     * @return
     */
    @PostMapping("/getBomMatList")
    public List<BomVo> getBomMatList(@RequestBody BomVo bomVo) {
        return bomService.getBomMatList(bomVo);
    }

    /**
     * 품목별소요량(원재료) 팝업 목록
     * @param itemCd
     * @return
     */
    @GetMapping("/getItemBomList/{id}")
    public List<BomRecipeVo> getItemBomList(@PathVariable("id") String itemCd) {
        return bomService.getItemBomList(itemCd);
    }

    /**
     * Bom 상세정보
     * @param bomId
     * @return
     */
    @GetMapping("/getBomInfo/{id}")
    public BomRequestVo getBomInfo(@PathVariable("id") String bomId) {
        BomRequestVo req = bomService.getBomInfo(bomId);
        return req;
    }

    /**
     *  BOM 전성분 데이터
     * @param prodCd
     * @return
     */
    @GetMapping("/getProductTypeInfo/{id}")
    public BomVo getProductTypeInfo(@PathVariable("id") String prodCd) {
        return bomService.getProductTypeInfo(prodCd);
    }

    /**
     * BOM 저장
     * @param request
     * @return
     * @throws Exception
     */
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

    /**
     * BOM VER저장
     * @param request
     * @return
     * @throws Exception
     */
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
