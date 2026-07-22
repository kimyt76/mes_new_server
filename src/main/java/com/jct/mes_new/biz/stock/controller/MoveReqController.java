package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.stock.service.MoveReqService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moveReq")
public class MoveReqController {
    private final MoveReqService moveReqService;
    private final MessageUtil messageUtil;

    /**
     *  등록일 seq 채번
     */
    @PostMapping("/getNextRegSeq")
    public int getNextRegSeq(@RequestBody MoveStockVo vo) {
        return moveReqService.getNextRegSeq(vo);
    }

    /**
     * 자재이동요청 조회
     * @param vo
     * @return
     */
    @PostMapping("/getMoveReqList")
    public List<MoveStockVo> getMoveReqList(@RequestBody MoveStockVo vo) {
        return moveReqService.getMoveReqList(vo);
    }

    /**
     * 자재이동 상세 조회
     */
    @GetMapping("/getMoveReqInfo/{id}")
    public MoveStockRequestVo getMoveReqInfo (@PathVariable("id") Long moveStockId) {
        return moveReqService.getMoveReqInfo(moveStockId);
    }

    /**
     * 공정별 자재이동 요청
     * @param vo
     * @return
     */
    @PostMapping("/saveProcMoveReq")
    public ResponseEntity<ApiResponse<?>> saveProcMoveReq (@RequestBody MoveStockRequestVo vo) {
        String result = moveReqService.saveProcMoveReq(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    /**
     * 자재이동 요청
     * @param vo
     * @return
     */
    @PostMapping("/saveMoveItem")
    public ResponseEntity<ApiResponse<?>> saveMoveItem (@RequestBody MoveStockRequestVo vo) {
        String result = moveReqService.saveMoveItem(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }

    /**
     * 자재이동 품목별 창고 재고 조회
     * @param vo
     * @return
     */
    @PostMapping("/getMoveReqStockList")
    public List<StockVo> getMoveReqStockList (@RequestBody StockVo vo) {
        return moveReqService.getMoveReqStockList(vo);
    }


    /**
     * 자재이동 요청
     * @param vo
     * @return
     */
    @PostMapping("/saveMoveReqComplete")
    public ResponseEntity<ApiResponse<?>> saveMoveReqComplete (@RequestBody MoveStockVo vo) {
        String result = moveReqService.saveMoveReqComplete(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }






}
