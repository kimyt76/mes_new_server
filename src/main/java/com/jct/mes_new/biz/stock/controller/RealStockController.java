package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.purchase.vo.TranItemVo;
import com.jct.mes_new.biz.stock.service.RealStockService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.config.common.ApiResponse;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/realStock")
public class RealStockController {

    private final RealStockService realStockService;
    private final MessageUtil messageUtil;

    /**
     * 실사 재고조회
     * @param vo
     * @return
     */
    @PostMapping("/getRealStockList")
    public List<RealStockVo> getRealStockList(@RequestBody RealStockVo vo) {
        return realStockService.getRealStockList(vo);
    }

    /**
     * 실사 재고 품목 리스트 조회
     * @param realStockMstId
     * @return
     */
    @GetMapping("/getRealStockItemList/{id}")
    public List<RealStockItemVo> getRealStockItemList(@PathVariable("id") Long realStockMstId) {
        return realStockService.getRealStockItemList(realStockMstId);
    }
    /**
     * 재고 품목 리스트 조회
     * @param vo
     * @return
     */
    @PostMapping("/getInvTranItemList")
    public List<TranItemVo> getInvTranItemList(@RequestBody TranItemVo vo) {
        return realStockService.getInvTranItemList(vo);
    }

    /**
     * 살사재고 mss 저장
     * @param vo
     * @return
     */
    @PostMapping("/saveRealStock")
    public ResponseEntity<ApiResponse<?>> saveRealStock (@RequestBody RealStockVo vo) {
        Long realStockMstId = realStockService.saveRealStock(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), Map.of("realStockMstId", realStockMstId)));
    }

    /**
     * 살사재고 품목 리스트 저장
     * @param vo
     * @return
     */
    @PostMapping("/saveRealStockItemList")
    public ResponseEntity<ApiResponse<?>> saveRealStockItemList (@RequestBody RealStockRequestVo vo) {
        Long realStockMstId = realStockService.saveRealStockItemList(vo);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created"), Map.of("realStockMstId", realStockMstId)));
    }

    /**
     * 월마감 완료 처리
     * @param realStockMstId
     * @return
     */
    @GetMapping("/saveRealStockComplete/{id}")
    public ResponseEntity<ApiResponse<?>> saveRealStockComplete (@PathVariable("id") Long realStockMstId) {
        String result = realStockService.saveRealStockComplete(realStockMstId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.created")));
    }


    @GetMapping("/deleteRealStock/{id}")
    public ResponseEntity<ApiResponse<?>> deleteRealStock (@PathVariable("id") Long realStockMstId) {
        String result = realStockService.deleteRealStock(realStockMstId);
        return ResponseEntity.ok(ApiResponse.ok(messageUtil.get("success.deleted")));
    }



}
