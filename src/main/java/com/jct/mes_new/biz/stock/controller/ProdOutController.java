package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.purchase.vo.TranRequestVo;
import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.service.ProdOutService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/prodOut")
public class ProdOutController {

    private final ProdOutService prodOutService;
    private final MessageUtil messageUtil;


    /**
     * 제품출고 리스트 조회
     * @param vo
     * @return
     */
    @PostMapping("/getProdOutList")
    public List<ProdOutVo> getProdOutList(@RequestBody ProdOutVo vo){
        return prodOutService.getProdOutList(vo);
    }

    /**
     * 제품출고 상세 조회
     * @param tranId
     * @return
     */
    @GetMapping("/getProdOutInfo/{id}")
    public ProdOutRequestVo getProdOutInfo(@PathVariable("id") Long tranId){
        return prodOutService.getProdOutInfo(tranId);
    }

    /**
     * 제품출고 등록
     * @param vo
     * @return
     */
    @PostMapping("/saveProdOut")
    public ResponseEntity<ApiResponse<?>> saveProdOut (@RequestBody ProdOutRequestVo vo) {
        String result = prodOutService.saveProdOut(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }





}
