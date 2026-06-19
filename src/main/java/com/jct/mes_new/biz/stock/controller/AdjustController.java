package com.jct.mes_new.biz.stock.controller;


import com.jct.mes_new.biz.purchase.vo.TranVo;
import com.jct.mes_new.biz.stock.service.AdjustService;
import com.jct.mes_new.biz.stock.vo.AdjustRequestVo;
import com.jct.mes_new.biz.stock.vo.AdjustVo;
import com.jct.mes_new.biz.stock.vo.MoveStockRequestVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adjust")
public class AdjustController {

    private final AdjustService adjustService;
    private final MessageUtil messageUtil;


    @PostMapping("/getAdjustList")
    public List<AdjustVo> getAdjustList(@RequestBody AdjustVo vo){
        return adjustService.getAdjustList(vo);
    }

    @GetMapping("/getAdjustInfo/{id}")
    public AdjustRequestVo getAdjustInfo(@PathVariable("id") Long tranId){
        return adjustService.getAdjustInfo(tranId);
    }

    /**
     * 자재조정 등록
     * @param vo
     * @return
     */
    @PostMapping("/saveAdjust")
    public ResponseEntity<ApiResponse<?>> saveAdjust (@RequestBody AdjustRequestVo vo) {
        String result = adjustService.saveAdjust(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


}
