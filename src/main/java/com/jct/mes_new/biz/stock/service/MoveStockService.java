package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.MoveStockVo;

import java.util.List;

public interface MoveStockService {
    List<MoveStockVo> getMoveStockList(MoveStockVo vo);
}
