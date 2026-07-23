package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.stock.service.MoveStockService;
import com.jct.mes_new.biz.stock.vo.MoveStockRequestVo;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moveStock")
public class MoveStockController {

    private final MoveStockService moveStockService;
    private final MessageUtil messageUtil;

    @PostMapping("getMoveStockList")
    public List<MoveStockVo> getMoveStockList (@RequestBody MoveStockVo vo){
        return moveStockService.getMoveStockList(vo);
    }

    /**
     * 자재이동 상세 조회
     */
    @GetMapping("/getMoveStockInfo/{id}")
    public MoveStockRequestVo getMoveStockInfo (@PathVariable("id") Long moveStockId) {
        return moveStockService.getMoveStockInfo(moveStockId);
    }

    @PostMapping("/saveMoveStockInfo")
    public ResponseEntity<ApiResponse<?>> saveMoveStockInfo (@RequestBody MoveStockRequestVo vo) {
        String result = moveStockService.saveMoveStockInfo(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    @PostMapping("/saveMoveStockConfirm")
    public ResponseEntity<ApiResponse<?>> saveMoveStockConfirm (@RequestBody MoveStockVo vo) {
        String result = moveStockService.saveMoveStockConfirm(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }





}
