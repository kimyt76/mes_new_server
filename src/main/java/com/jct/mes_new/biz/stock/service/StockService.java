package com.jct.mes_new.biz.stock.service;

import com.jct.mes_new.biz.stock.vo.ItemUseVo;
import com.jct.mes_new.biz.stock.vo.StockHistResponseVo;
import com.jct.mes_new.biz.stock.vo.StockVo;

import java.util.List;
import java.util.Map;

public interface StockService {

    Map<String, Object> getStockItemList(StockVo vo);

    StockHistResponseVo getStockItemHistList(StockVo vo);


    List<ItemUseVo> getItemUseList(ItemUseVo vo);
}
