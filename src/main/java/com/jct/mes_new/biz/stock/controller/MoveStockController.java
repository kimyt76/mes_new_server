package com.jct.mes_new.biz.stock.controller;

import com.jct.mes_new.biz.stock.service.MoveStockService;
import com.jct.mes_new.biz.stock.vo.MoveStockVo;
import com.jct.mes_new.config.common.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
