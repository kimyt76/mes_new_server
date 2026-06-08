package com.jct.mes_new.biz.stock.controller;


import com.jct.mes_new.biz.stock.service.StockService;
import com.jct.mes_new.biz.stock.vo.ItemUseVo;
import com.jct.mes_new.biz.stock.vo.StockHistResponseVo;
import com.jct.mes_new.biz.stock.vo.StockVo;
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


}
