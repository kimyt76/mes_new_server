package com.jct.mes_new.biz.stock.controller;


import com.jct.mes_new.biz.stock.service.StockService;
import com.jct.mes_new.biz.stock.vo.*;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/stock")
public class StockController {

    private final StockService stockService;
    private final MessageUtil messageUtil;


    /**
     * 재고조회
     * @param vo
     * @return
     */
    @PostMapping("/getStockItemList")
    public Map<String, Object> getStockItemList(@RequestBody StockVo vo) {
        return stockService.getStockItemList(vo);
    }


    /**
     * 칭량, 충전, 포장에 사용
     * @param vo
     * @return
     */
    @PostMapping("/getStockItemHistList")
    public StockHistResponseVo getStockItemHistList(@RequestBody StockVo vo) {
        return stockService.getStockItemHistList(vo);
    }

    /**
     * 품목별 사용량
     */
    @PostMapping("/getItemUseList")
    public List<ItemUseVo> getItemUseList(@RequestBody ItemUseVo vo) {
        return stockService.getItemUseList(vo);
    }

    /**
     * 사용기한 (원재료)
     * @param vo
     * @return
     */
    @PostMapping("/getUseByM1List")
    public Map<String, Object> getUseByM1List(@RequestBody UseByVo vo) {
        return stockService.getUseByM1List(vo);
    }
    /**
     * 사용기한 (부자재)
     * @param vo
     * @return
     */
    @PostMapping("/getUseByM2List")
    public List<UseByVo> getUseByM2List(@RequestBody UseByVo vo) {
        return stockService.getUseByM2List(vo);
    }

    /**
     * 원재료, 부자재 수불부 조회
     * @param vo
     * @return
     */
    @PostMapping("/getTranLedger")
    public List<TranLedgerVo> getTranLedger(@RequestBody TranLedgerVo vo) {
        return stockService.getTranLedger(vo);
    }


    /**
     * 시험번호별 사용현황
     * @param vo
     * @return
     */
    @PostMapping("/getTestUseList")
    public List<TestNoProdVo> getTestUseList(@RequestBody TestNoProdVo vo) {
        return stockService.getTestUseList(vo);
    }

}
